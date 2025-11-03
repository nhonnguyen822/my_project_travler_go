package com.example.tourtravelserver.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GeoLocationService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public GeoLocationService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Lấy toạ độ (lat, lon) từ tên địa điểm bằng OpenStreetMap API (Nominatim)
     *
     * @param placeName Tên địa điểm (VD: "Hà Nội", "Vịnh Hạ Long")
     * @return Mảng gồm [latitude, longitude] hoặc null nếu không tìm thấy
     */
    public double[] getCoordinates(String placeName) {
        try {
            // 🗺️ API của OpenStreetMap (Nominatim)
            String url = String.format(
                    "https://nominatim.openstreetmap.org/search?q=%s&format=json&limit=1",
                    placeName.replace(" ", "+")
            );

            // Gửi request GET
            String response = restTemplate.getForObject(url, String.class);

            // Parse JSON bằng Jackson (Spring có sẵn)
            JsonNode jsonArray = objectMapper.readTree(response);

            if (jsonArray.isArray() && jsonArray.size() > 0) {
                JsonNode loc = jsonArray.get(0);
                double lat = loc.get("lat").asDouble();
                double lon = loc.get("lon").asDouble();
                return new double[]{lat, lon};
            }

        } catch (Exception e) {
            System.err.println("⚠️ Lỗi lấy tọa độ: " + e.getMessage());
        }

        return null;
    }
}
