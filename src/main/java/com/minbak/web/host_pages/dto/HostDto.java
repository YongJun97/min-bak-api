package com.minbak.web.host_pages.dto;

import com.minbak.web.file_upload.ImageFileDto;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class HostDto {
    private Integer roomId;       // 숙박 시설 고유 ID
    private Integer userId;
    private String userName;
    private String userEmail;
    private String name;          // 숙박 시설 이름
    private String title;         // 숙소 제목
    private String content;       // 숙박 시설 설명
    private String address;       // 숙박 시설 주소
    private Integer price;        // 숙박 가격
    private String useGuide;      // 숙박 이용 안내
    private Double latitude;      // 위도
    private Double longitude;     // 경도
    private Integer maxGuests;    // 최대 숙박 인원
    private Integer bedrooms;     // 침실 개수
    private Integer beds;         // 침대 개수
    private Integer bathrooms;    // 욕실 개수
    private String buildingType;  // 건물 유형 (아파트, 주택, 빌라)

    private List<ImageFileDto> files;
    private List<Integer> categoryIds;
    private List<String> fileUrls;
   // private List<CreateImageDto> imageFiles; // 추가: 이미지 URL 리스트
    private List<Integer> optionIds; // 선택한 옵션 ID 리스트 (rooms_room_options 테이블 저장용)
}
