package com.hellofit.hellofit_server.food;

import com.hellofit.hellofit_server.food.dto.FoodResponseDto;
import com.hellofit.hellofit_server.global.dto.CursorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.UUID;

@Tag(name = "음식 API", description = "식품 CSV 업로드 및 관리 API")
@RestController
@RequestMapping("/foods")
@RequiredArgsConstructor
public class FoodController {

    private final FoodService foodService;

    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    @Operation(summary = "CSV 파일 업로드", description = "CSV 파일을 업로드하여 foods 테이블에 저장합니다.")
    public ResponseEntity<String> uploadCsv(
        @RequestParam("file")
        @Parameter(description = "업로드할 CSV 파일", required = true,
            content = @Content(mediaType = "multipart/form-data",
                schema = @Schema(type = "string", format = "binary")))
        MultipartFile file
    ) {
        int count = foodService.saveFoodsFromCsv(file);
        return ResponseEntity.ok(count + " rows inserted.");
    }

    @GetMapping("/search")
    @Operation(summary = "음식 검색 (cursor 기반)", description = "createdAt 기준 cursor 방식으로 음식 데이터를 검색합니다.")
    public CursorResponse<FoodResponseDto.Summary> searchFoods(
        @RequestParam(required = false, defaultValue = "") String keyword,
        @RequestParam(required = false) UUID cursor,
        @RequestParam(defaultValue = "10") int size
    ) {
        return foodService.searchFoods(keyword, cursor, size);
    }
}
