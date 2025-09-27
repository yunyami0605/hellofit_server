package com.hellofit.hellofit_server.food;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Food API", description = "식품 CSV 업로드 및 관리 API")
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
}
