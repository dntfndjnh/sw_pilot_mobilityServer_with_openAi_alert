package com.sw_pilot.mobility_server.controller;

import com.sw_pilot.mobility_server.domain.Data;
import com.sw_pilot.mobility_server.domain.ReportItem;
import com.sw_pilot.mobility_server.service.OpenAiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Controller
public class SwpilotController {

   //@GetMapping("/report")
    //public String Show_report(){
      //  return "report";
    //}

    @RestController
    public class EchoController {

        @GetMapping("/test")
        public String echo(@RequestParam(name = "msg", required = false, defaultValue = "메시지가 없습니다.") String msg) {
            return msg; // 입력받은 문자열을 그대로 반환
        }
    }



    @GetMapping("/report")
    public String reportPage(@RequestParam(value = "id", required = false) String id, Model model) {
        if (id == null || id.trim().isEmpty()) {
            id = "알 수 없음";
        }

        model.addAttribute("reportTitle", "공정점검보고서");
        model.addAttribute("authorName", id);
        model.addAttribute("writeDate", ""); // JS로 동적 날짜 입력 예정
        model.addAttribute("summary", "여기에 보고서 요약 내용이 들어갑니다.");

        // 상세 항목 예시
        model.addAttribute("details", java.util.List.of(
                new ReportItem("온도", "현재 측정된 온도", "24.5°C"),
                new ReportItem("습도", "현재 측정된 습도", "45%"),
                new ReportItem("CO2", "이산화탄소 농도", "400ppm")
        ));

        return "report"; // src/main/resources/templates/report.html
    }


    @GetMapping("/play")
    public String play() {
        return "play";
    }


    @RestController
    @RequestMapping("/api")
    public class DataController {
        private final List<Data> dataList = new CopyOnWriteArrayList<>();
        private final Map<String, Integer> itemCounts = new ConcurrentHashMap<>();

        @PostMapping("/send")
        public String receiveData(@RequestBody Data data) {
            LocalDateTime now = LocalDateTime.now();
            String time = now.format(DateTimeFormatter.ofPattern("HH시 mm분 ss초"));
            data.setTime(time);
            dataList.add(data);
            itemCounts.merge(data.getItemName(), data.getValue(), Integer::sum);
            System.out.println("Received " + data.getAreaName() + " - " + data.getItemName() + " = " + data.getValue() + " at " + time);
            return "Received " + data.getAreaName() + " - " + data.getItemName() + " = " + data.getValue() + " at " + time;
        }

        @GetMapping("/zone-totals")
        public List<Data> getZoneTotals() {
            return dataList;
        }

        @GetMapping("/totals")
        public Map<String, Integer> getItemTotals() {
            return itemCounts;
        }

        @GetMapping("/analyze")
        public String analyzeData() throws IOException {
            return OpenAiService.analyzeDataWithGpt(dataList, itemCounts);
        }
    }
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @PostMapping("/imagesend")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("파일이 비어있습니다.");
        }

        try {
            // 저장 디렉토리
            String homePath = System.getProperty("user.home").replace("\\", "/");
            String uploadDir = homePath + "/Documents/captured/";
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // 확장자 처리
            String extension = ".jpg";
            String originalFilename = file.getOriginalFilename();
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            // 이미지 로드
            BufferedImage originalImage = ImageIO.read(file.getInputStream());
            if (originalImage == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("이미지 파일이 아닙니다.");
            }

            Graphics2D g = originalImage.createGraphics();

            // 타임스탬프 텍스트
            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

            // 폰트 및 크기 설정
            Font font = new Font("SansSerif", Font.BOLD, 24);
            g.setFont(font);
            FontMetrics fm = g.getFontMetrics();

            // 위치 계산
            int textWidth = fm.stringWidth(timestamp);
            int textHeight = fm.getHeight();
            int padding = 10;
            int x = 10;
            int y = originalImage.getHeight() - padding;

            // 배경 박스 (검정색)
            g.setColor(new Color(0, 0, 0, 180)); // 반투명 검정
            g.fillRect(x - 5, y - textHeight, textWidth + 10, textHeight + 5);

            // 텍스트 (흰색)
            g.setColor(Color.WHITE);
            g.drawString(timestamp, x, y - 5); // 살짝 위로 올려줌

            g.dispose();

            // 저장
            String newFileName = "latest" + extension;
            Path filePath = Paths.get(uploadDir, newFileName);
            ImageIO.write(originalImage, extension.replace(".", ""), filePath.toFile());
            messagingTemplate.convertAndSend("/topic/alert", "alert");
            return ResponseEntity.ok("파일이 업로드되었습니다: /uploads/" + newFileName);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("파일 업로드 중 오류 발생");
        }
    }

    public class AlertController {

        private final SimpMessagingTemplate messagingTemplate;

        public AlertController(SimpMessagingTemplate messagingTemplate) {
            this.messagingTemplate = messagingTemplate;
        }

        public void sendAlert() {
            messagingTemplate.convertAndSend("/topic/alert", "alert");
        }
    }

}












