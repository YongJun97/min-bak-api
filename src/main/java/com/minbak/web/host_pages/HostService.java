package com.minbak.web.host_pages;

import com.minbak.web.file_upload.FileMapper;
import com.minbak.web.file_upload.ImageFileDto;
import com.minbak.web.host_pages.dto.HostDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
public class HostService{

    @Autowired
    private CreateHostMapper createHostMapper;
    @Autowired
    private FileMapper fileMapper;
    @Value("${file.upload.directory}")
    private String uploadDirectory;

    @Transactional
    public void insertRoom(HostDto hostDto) {
        // 🏡 1. 숙소 정보 `rooms` 테이블에 저장
        createHostMapper.insertRoom(hostDto);

    }

    // ✅ HostDto에 있는 files 리스트를 기반으로 image_files 테이블에 insert 수행
    public void insertRoomImages(HostDto hostDto, int roomId) {
        List<ImageFileDto> imageFiles = hostDto.getFiles();
        if (imageFiles == null || imageFiles.isEmpty()) return;

        // 각 이미지 정보에 필요한 기본 값 세팅
        for (ImageFileDto image : imageFiles) {
            if (!image.getFileUrl().startsWith("/uploads/")) {
                image.setFileUrl("/uploads/" + image.getFileUrl());
            }
            if (image.getFileName() == null || image.getFileName().isEmpty()) {
                String fileUrl = image.getFileUrl();
                image.setFileName(fileUrl.substring(fileUrl.lastIndexOf("_") + 1));
            }
            if (image.getEntityType() == null) {
                image.setEntityType("rooms");
            }
            image.setEntityId(roomId);
            image.setUserId(hostDto.getUserId());
            if (image.getFileSize() == 0) {
                image.setFileSize(0); // 실제 파일 사이즈가 필요한 경우 업로드 시점에서 세팅해야 함
            }
        }

        hostDto.setFiles(imageFiles); // Mapper에서 foreach 사용을 위해 다시 설정
        createHostMapper.insertRoomImages(hostDto, roomId);
    }




    // ✅ HostDto에 있는 fileUrls (base64) 리스트를 기반으로 image_files 테이블에 insert 수행
    public void insertRoomImagesFromHostDto(HostDto hostDto, int roomId) {
        List<String> base64Images = hostDto.getFileUrls();
        if (base64Images == null || base64Images.isEmpty()) {
            System.out.println("❌ fileUrls is null or empty");
            return;
        }

        System.out.println("📦 fileUrls from hostDto: " + base64Images);

        List<ImageFileDto> imageFiles = new ArrayList<>();

        for (String base64Data : base64Images) {
            try {
                // 1. Base64 prefix 제거 (예: data:image/jpeg;base64,...)
                String base64 = base64Data.split(",", 2)[1];

                // 2. MIME 타입으로 확장자 결정
                String mimeType = base64Data.substring(base64Data.indexOf(":") + 1, base64Data.indexOf(";"));
                String extension = switch (mimeType) {
                    case "image/png" -> ".png";
                    case "image/gif" -> ".gif";
                    case "image/jpeg", "image/jpg" -> ".jpg";
                    default -> ".jpg";
                };

                // 3. 파일명 생성 및 저장 경로
                String uniqueFilename = UUID.randomUUID().toString() + extension;
                Path filePath = Paths.get(uploadDirectory, uniqueFilename);

                // 4. 디코딩 후 저장
                byte[] imageBytes = Base64.getDecoder().decode(base64);
                Files.write(filePath, imageBytes);

                // 5. DTO 생성 및 리스트에 추가
                ImageFileDto imageFile = new ImageFileDto();
                imageFile.setFileUrl("/uploads/" + uniqueFilename);
                imageFile.setFileName(uniqueFilename);
                imageFile.setFileSize(imageBytes.length);
                imageFile.setEntityType("rooms");
                imageFile.setEntityId(roomId);
                imageFile.setUserId(hostDto.getUserId());

                imageFiles.add(imageFile);

            } catch (Exception e) {
                System.out.println("❌ 이미지 저장 실패: " + e.getMessage());
            }
        }

        if (!imageFiles.isEmpty()) {
            hostDto.setFiles(imageFiles); // 이 필드는 mapper insertRoomImages 에서 사용됨
            createHostMapper.insertRoomImages(hostDto, roomId);
            System.out.println("✅ 이미지 저장 완료: " + imageFiles.size() + "개");
        } else {
            System.out.println("⚠️ 저장할 이미지가 없습니다");
        }
    }





    public ImageFileDto saveFile(String uniqueFilename, String originalFilename ,int fileSize, int roomId, String type) throws IOException {

        // 파일 정보 DB 저장
        ImageFileDto imageFile = new ImageFileDto();
        //웹에서 사진을 다운받을 경로
        imageFile.setFileUrl("/uploads/" + uniqueFilename);
        //기존 파일 이름
        imageFile.setFileName(originalFilename);
        //파일 사이즈 인트로변환
        imageFile.setFileSize(fileSize);
        //rooms의 사진이라는 뜻
        imageFile.setEntityType(type); // type 타입으로 저장
        //해당 room의Id
        imageFile.setEntityId(roomId);
        // TODO 유저 아이디 추가
        fileMapper.insertImageFile(imageFile);

        return imageFile;
    }

    public void updateRoomImages(String fileUrl, int roomId) {
        // 이미지를 업데이트하는 쿼리 호출
        createHostMapper.updateRoomImages("/uploads/"+ fileUrl, roomId);
    }
}
