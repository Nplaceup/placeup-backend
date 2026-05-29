package com.dontworry.api.infra.crawler;

import com.dontworry.api.common.exception.CustomException;
import com.dontworry.api.common.constant.ApiResponseCode;
import com.dontworry.api.infra.crawler.dto.PlaceInfoResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlaceHtmlClient {

    private static final String PLACE_HOME_URL = "https://pcmap.place.naver.com/place/%s/home";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    private record PlaceApolloData(
            String address,
            String phoneNumber,
            String roadAddress,
            String talktalkUrl,
            Boolean reservationAvailable
    ) {}

    public Long extractPlaceIdFromUrl(String url) {
        String idStr = url.replaceAll(".*place/(\\d+).*", "$1");
        if (!idStr.equals(url) && idStr.matches("\\d+")) {
            return Long.valueOf(idStr);
        }
        String resolvedUrl = resolveRedirectUrl(url);
        log.info("[Validate UserPlace] Resolved URL: {}", resolvedUrl);
        idStr = resolvedUrl.replaceAll(".*place/(\\d+).*", "$1");
        if (!idStr.equals(resolvedUrl) && idStr.matches("\\d+")) {
            return Long.valueOf(idStr);
        }
        throw new CustomException(ApiResponseCode.INVALID_URL);
    }

    public PlaceInfoResponse validateUserPlacesUrl(String url) {
        try {
            Long placeId = extractPlaceIdFromUrl(url);
            log.info("[Validate UserPlace] Place Id: {}", placeId);

            String html = fetchPlaceHtml(placeId);
            Document doc = Jsoup.parse(html);

            String placeName = parsePlaceName(doc);
            String category  = parseCategory(doc);
            PlaceApolloData apolloData = parseFromApolloState(html, placeId);

            log.info("[Validate UserPlace End] placeId: {}, placeName: {}, category: {}, address: {}, phoneNumber: {}, talktalkUrl: {}",
                    placeId, placeName, category, apolloData.address(), apolloData.phoneNumber(), apolloData.talktalkUrl());

            return PlaceInfoResponse.toDto(
                    placeId,
                    placeName,
                    category,
                    apolloData.address(),
                    apolloData.phoneNumber(),
                    apolloData.roadAddress(),
                    apolloData.talktalkUrl(),
                    apolloData.reservationAvailable()
            );

        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            log.error("[Validate UserPlace] 크롤링 실패: {}", e.getMessage(), e);
            throw new CustomException(ApiResponseCode.FAIL);
        }
    }

    private PlaceApolloData parseFromApolloState(String html, Long placeId) {
        try {
            Matcher matcher = Pattern.compile(
                    "window\\.__APOLLO_STATE__\\s*=\\s*(\\{.*?\\});",
                    Pattern.DOTALL
            ).matcher(html);

            if (!matcher.find()) {
                log.warn("[APOLLO_STATE] 없음, fallback");
                return fallback(html);
            }

            JsonNode placeNode = objectMapper.readTree(matcher.group(1))
                    .get("PlaceDetailBase:" + placeId);

            if (placeNode == null) {
                log.warn("[APOLLO_STATE] placeNode 없음, fallback");
                return fallback(html);
            }

            // 주소: 지번 우선, 없으면 도로명
            String address = getText(placeNode, "address");
            if (address.isBlank()) address = getText(placeNode, "roadAddress");
            if (address.isBlank()) address = parseAddressFromHtml(Jsoup.parse(html));

            // 전화번호: virtualPhone 우선, 없으면 phone
            String phoneNumber = getText(placeNode, "virtualPhone");
            if (phoneNumber.isBlank()) phoneNumber = getText(placeNode, "phone");
            if (phoneNumber.isBlank()) phoneNumber = null;

            String roadAddress = getText(placeNode, "roadAddress");
            if (roadAddress.isBlank()) roadAddress = null;

            String talktalkUrl = getText(placeNode, "talktalkUrl");
            if (talktalkUrl.isBlank()) talktalkUrl = null;

            // conveniences 배열에 "예약" 포함 여부
            boolean reservationAvailable = false;
            JsonNode conveniences = placeNode.get("conveniences");
            if (conveniences != null && conveniences.isArray()) {
                for (JsonNode c : conveniences) {
                    if ("예약".equals(c.asText())) {
                        reservationAvailable = true;
                        break;
                    }
                }
            }

            log.info("[APOLLO_STATE] phoneNumber={}, talktalkUrl={}, reservationAvailable={}",
                    phoneNumber, talktalkUrl, reservationAvailable);

            return new PlaceApolloData(address, phoneNumber, roadAddress, talktalkUrl, reservationAvailable);

        } catch (Exception e) {
            log.warn("[APOLLO_STATE] 파싱 실패: {}, fallback", e.getMessage());
            return fallback(html);
        }
    }

    private PlaceApolloData fallback(String html) {
        String address = parseAddressFromHtml(Jsoup.parse(html));
        return new PlaceApolloData(address, null, null, null, null);
    }

    private String getText(JsonNode node, String field) {
        JsonNode n = node.get(field);
        if (n == null || n.isNull()) return "";
        return n.asText("").trim();
    }

    private String resolveRedirectUrl(String url) {
        try {
            org.jsoup.Connection.Response response = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                            "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36")
                    .followRedirects(true)
                    .execute();
            return response.url().toString();
        } catch (Exception e) {
            log.error("[Validate UserPlace] URL 리다이렉트 추적 실패: {}", e.getMessage());
            throw new CustomException(ApiResponseCode.INVALID_URL);
        }
    }

    private String fetchPlaceHtml(Long placeId) {
        String targetUrl = String.format(PLACE_HOME_URL, placeId);
        log.info("[Validate UserPlace] Fetching HTML: {}", targetUrl);

        HttpHeaders headers = buildCommonHeaders();
        headers.set(HttpHeaders.REFERER, "https://map.naver.com/");

        ResponseEntity<byte[]> response = restTemplate.exchange(
                targetUrl, HttpMethod.GET, new HttpEntity<>(headers), byte[].class);

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            log.error("[Validate UserPlace] HTML fetch 실패, status: {}", response.getStatusCode());
            throw new CustomException(ApiResponseCode.FAIL);
        }

        return new String(response.getBody(), StandardCharsets.UTF_8);
    }

    private String parsePlaceName(Document doc) {
        Element nameEl = doc.selectFirst("span.GHAhO");
        if (nameEl != null && !nameEl.text().trim().isEmpty()) {
            log.info("[Validate UserPlace] Place Name: {}", nameEl.text().trim());
            return nameEl.text().trim();
        }
        Element ogTitle = doc.selectFirst("meta[property=og:title]");
        if (ogTitle != null && !ogTitle.attr("content").trim().isEmpty()) {
            log.info("[Validate UserPlace] Place Name (og:title): {}", ogTitle.attr("content").trim());
            return ogTitle.attr("content").trim();
        }
        throw new CustomException(ApiResponseCode.FAIL);
    }

    private String parseCategory(Document doc) {
        Element categoryEl = doc.selectFirst("span.lnJFt");
        if (categoryEl != null && !categoryEl.text().trim().isEmpty()) {
            log.info("[Validate UserPlace] Category: {}", categoryEl.text().trim());
            return categoryEl.text().trim();
        }
        Element ogDesc = doc.selectFirst("meta[property=og:description]");
        if (ogDesc != null) {
            String[] parts = ogDesc.attr("content").split("[,·\\s]+");
            if (parts.length > 0 && !parts[0].trim().isEmpty()) {
                log.info("[Validate UserPlace] Category (og:description): {}", parts[0].trim());
                return parts[0].trim();
            }
        }
        return "";
    }

    private String parseAddressFromHtml(Document doc) {
        for (Element row : doc.select("div.nQ7Lh")) {
            Element label = row.selectFirst("span.TjXg1");
            if (label != null && "지번".equals(label.text().trim())) {
                String address = row.text().replace("지번", "").replace("복사", "").trim();
                log.info("[Validate UserPlace] Address (HTML 지번): {}", address);
                return address;
            }
        }
        for (Element row : doc.select("div.nQ7Lh")) {
            Element label = row.selectFirst("span.TjXg1");
            if (label != null && label.text().contains("도로명")) {
                String address = row.text().replace("도로명", "").replace("복사", "").trim();
                log.info("[Validate UserPlace] Address (HTML 도로명): {}", address);
                return address;
            }
        }
        log.warn("[Validate UserPlace] 주소를 찾을 수 없습니다.");
        return "";
    }

    private HttpHeaders buildCommonHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.USER_AGENT,
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                        "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36");
        headers.set(HttpHeaders.ACCEPT_LANGUAGE, "ko-KR,ko;q=0.9");
        headers.set(HttpHeaders.ACCEPT, "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        return headers;
    }
}