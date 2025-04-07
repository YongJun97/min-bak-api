package com.minbak.web.host_pages;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.minbak.web.file_upload.FileService;
import com.minbak.web.file_upload.ImageFileDto;
import com.minbak.web.host_pages.dto.CreateImageDto;
import com.minbak.web.spring_security.CustomUserDetails;
import com.minbak.web.spring_security.CustomUserDetailsService;
import com.minbak.web.spring_security.jwt.JwtUtil;
import com.minbak.web.users.RoleDto;
import com.minbak.web.users.UsersService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.catalina.Host;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.ui.Model;
import com.minbak.web.host_pages.dto.HostDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/user")
public class HostController {
    @Autowired
    private HostService hostService;
    @Autowired
    private GetUserNameService getUserNameService;
    @Autowired
    private FileService fileService;
    @Autowired
    private CreateHostMapper createHostMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @ModelAttribute("hostDto")
    public HostDto hostDto() {
        return new HostDto(); // 세션에 없으면 새로운 객체 반환
    }

    @GetMapping("/create-rooms")
    public String createRooms(@AuthenticationPrincipal CustomUserDetails userDetails,
                              @ModelAttribute("hostDto") HostDto hostDto,
                              Model model) {
        // userId가 설정되지 않았다면 로그인된 사용자의 ID(1번) 설정
        if(userDetails != null){
            hostDto.setUserId(userDetails.getUserId());
            System.out.println("로그인한 userId: " + userDetails.getUserId());
        }

        // userName이 아직 설정되지 않았다면 DB에서 가져오기
        if (hostDto.getUserName() == null) {
            String userName = getUserNameService.getUserName(hostDto.getUserId());
            hostDto.setUserName(userName);
            System.out.println("DB에서 가져온 userName: " + userName); // 로그 확인
        }

        model.addAttribute("name", hostDto.getUserName());
        return "host-pages/rooms_create";
    }

    @GetMapping("/overview")
    public String overview(){
        return "host-pages/overview";
    }

    @GetMapping("/place")
    public String place(){
        return "host-pages/place";
    }
    // 숙소 유형 선택 페이지
    @GetMapping("/type")
    public String roomsType(){
        return "host-pages/type";
    }
    // 숙소 유형 저장 후 다음 페이지 이동
    //@PostMapping("/type/save")
    //@ResponseBody // AJAX 응답
    //public String saveBuildingType(@ModelAttribute("hostDto") HostDto hostDto, @RequestParam("buildingType") String buildingType) {
    //    hostDto.setBuildingType(buildingType);
    //    System.out.println("🏡 선택한 숙소 유형 저장: " + hostDto.getBuildingType()); // ✅ 콘솔 로그 확인
    //     return "success"; // AJAX 요청 완료 응답
    //}


    @GetMapping("/location")
    public String roomLocation(){
        return "host-pages/location";
    }


    @GetMapping("/floor-plan")
    public String floorPlan(){
        return "host-pages/floor-plan";
    }

    @GetMapping("/charm")
    public String charm(){
        return "host-pages/accommodation-charm";
    }

    @GetMapping("/option")
    public String roomsOption(){
        return "host-pages/option";
    }

    @GetMapping("/category")
    public String roomsCategories() {
        return "host-pages/category";
    }

    @GetMapping("/photos")
    public String photos(){
        return "host-pages/photos";
    }
    @Value("${file.upload.directory}")
    private String uploadDirectory;


    @GetMapping("/roomName")
    public String roomsName(){
        return "host-pages/roomName";
    }


    @GetMapping("/title")
    public String roomsTitle(){
        return "host-pages/title";
    }


    @GetMapping("/description")
    public String description(){
        return "host-pages/description";
    }


    @GetMapping("useGuide")
    public String roomsUseGuide(){
        return "host-pages/useGuide";
    }


    @GetMapping("/finish-setup")
    public String finish(){
        return "host-pages/finish-setup";
    }

    @GetMapping("/price")
    public String roomsPrice(){
        return "host-pages/price";
    }


    @GetMapping("/receipt")
    public String reviewPage() {

        return "host-pages/receipt";
    }


    @GetMapping("/publish")
    public String publish(){
        return "host-pages/publish";
    }

    @Autowired
    UsersService usersService;

    @Autowired
    CustomUserDetailsService customUserDetailsService;

    @Value("${jwt.refresh-token-expiration-time}")
    private long REFRESH_TOKEN_EXPIRATION_TIME;

    // ✅ 최종 등록 페이지 (숙소 등록 요청)
    @PostMapping("/register")
    public String registerRoom(@ModelAttribute HostDto hostDto,
                               HttpServletRequest request,
                               HttpServletResponse response){

        List<String> fileUrls = hostDto.getFileUrls();

        if (fileUrls != null) {
            for (String url : fileUrls) {
                System.out.println("URL: " + url);
            }
        }

        System.out.println("📦 fileUrls from hostDto: " + hostDto.getFileUrls());
        System.out.println("📦 fileUrls from request: " + request.getParameter("fileUrls"));

//        List<String> fileUrls = hostDto.getFileUrls();  // HostDto에서 fileUrls를 가져옴
//
//        hostService.insertRoom(hostDto);
//        int roomId = hostDto.getRoomId();  // 생성된 roomId를 가져옴
//        createHostMapper.insertRoomOptions(hostDto.getRoomId(),hostDto.getOptionIds());
//        createHostMapper.insertRoomCategories(hostDto.getRoomId(), hostDto.getCategoryIds());
//        for (String fileUrl : fileUrls){
//            hostService.insertRoomImages(fileUrl, roomId);
//        }
//        for (RoleDto role : usersService.findRolesByUserId(hostDto.getUserId())){
//            if(role.getRole().equals("ROLE_HOST")){
//                return "redirect:/host/today";
//            }
//        }
//
//        usersService.createHostRoleByUserIdAndRoleId(hostDto.getUserId(),2);
//
//        String username = usersService.findUserEmailByUserId(hostDto.getUserId());
//
//        String refreshToken = jwtUtil.getRefreshTokenFromCookies(request);
//        usersService.deleteRefreshTokenDataByRefreshToken(refreshToken);
//
//        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
//
//        //해당 인증객체의 roles가져와서
//        List<String> roles = userDetails.getAuthorities().stream()
//                .map(GrantedAuthority::getAuthority)
//                .collect(Collectors.toList());
//
//        // 새로운 Access Token 생성
//        String newAccessToken = jwtUtil.generateAccessToken(username, roles);
//        String newRefreshToken = jwtUtil.generateRefreshToken(username);
//
//        //토큰을 createRefreshCookie메서드(아래정의됨)로 쿠키에 추가
//        response.addCookie(jwtUtil.createRefreshCookie("refreshToken", newRefreshToken));
//
//        //엑세스토큰도 생성해서 쿠키로 전달
//        response.addCookie(jwtUtil.createAccessCookie("jwtToken",newAccessToken));
//
//        usersService.createRefreshTokenData(username,newRefreshToken, REFRESH_TOKEN_EXPIRATION_TIME);
//
//
//        usersService.insertNewHost(hostDto.getUserId(),"미검증");
//        return "redirect:/host/today";


        // ✅ fileUrls만 수동 파싱
        String[] fileUrlsArray = request.getParameterValues("fileUrls");
        if (fileUrlsArray != null) {
            hostDto.setFileUrls(Arrays.asList(fileUrlsArray));
        }


        // 1️⃣ rooms 테이블에 숙소 정보 저장
        hostService.insertRoom(hostDto);
        int roomId = hostDto.getRoomId();

        // 2️⃣ 옵션, 카테고리 정보 저장
        createHostMapper.insertRoomOptions(roomId, hostDto.getOptionIds());
        createHostMapper.insertRoomCategories(roomId, hostDto.getCategoryIds());

        // ✅ 3️⃣ 이미지 디코딩 후 image_files 테이블에 저장
        hostService.insertRoomImagesFromHostDto(hostDto, roomId);



        // 4️⃣ 사용자 role 검사 및 추가
        for (RoleDto role : usersService.findRolesByUserId(hostDto.getUserId())) {
            if (role.getRole().equals("ROLE_HOST")) {
                return "redirect:/host/today";
            }
        }
        usersService.createHostRoleByUserIdAndRoleId(hostDto.getUserId(), 2);

        // 5️⃣ 토큰 재발급 및 쿠키 갱신
        String username = usersService.findUserEmailByUserId(hostDto.getUserId());
        String refreshToken = jwtUtil.getRefreshTokenFromCookies(request);
        usersService.deleteRefreshTokenDataByRefreshToken(refreshToken);

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        String newAccessToken = jwtUtil.generateAccessToken(username, roles);
        String newRefreshToken = jwtUtil.generateRefreshToken(username);
        response.addCookie(jwtUtil.createRefreshCookie("refreshToken", newRefreshToken));
        response.addCookie(jwtUtil.createAccessCookie("jwtToken", newAccessToken));

        usersService.createRefreshTokenData(username, newRefreshToken, REFRESH_TOKEN_EXPIRATION_TIME);

        usersService.insertNewHost(hostDto.getUserId(), "미검증");

        System.out.println("📸 fileUrls = " + hostDto.getFileUrls());

        return "redirect:/host/today";

    }
}
