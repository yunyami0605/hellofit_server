package com.hellofit.hellofit_server.food;

import com.hellofit.hellofit_server.food.dto.FoodResponseDto;
import com.hellofit.hellofit_server.global.dto.CursorResponse;
import com.opencsv.CSVReader;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
@Service
public class FoodService {

    private final FoodRepository foodRepository;

    /**
     * food db 데이터 초기화
     */
    public void deleteAllFoods() {
        foodRepository.deleteAllInBatch();
    }

    public int saveFoodsFromCsv(MultipartFile file) {
        try {
            byte[] data = file.getBytes();
            int offset = 0;
            Charset charset = detectCharset(data);
            // If UTF-8 with BOM, skip BOM bytes
            if (charset.equals(StandardCharsets.UTF_8) && hasUtf8Bom(data)) {
                offset = 3;
            }
            try (BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(data, offset, data.length - offset), charset));
                 CSVReader reader = new CSVReader(br)) {
                String[] nextLine;
                List<FoodEntity> foods = new ArrayList<>();
                // skip header
                reader.readNext();
                while ((nextLine = reader.readNext()) != null) {
                    if (nextLine.length == 0) continue;
                    FoodEntity food = FoodEntity.builder()
                        .foodCode(getSafe(nextLine, 0))          // 식품코드
                        .foodName(getSafe(nextLine, 1))          // 식품명
                        .category(getSafe(nextLine, 2))          // 식품대분류명
                        .repFoodName(getSafe(nextLine, 3))       // 대표식품명
                        .kcal(parseFloat(getSafe(nextLine, 4)))  // 에너지(kcal)
                        .protein(parseFloat(getSafe(nextLine, 5)))   // 단백질(g)
                        .fat(parseFloat(getSafe(nextLine, 6)))       // 지방(g)
                        .carbs(parseFloat(getSafe(nextLine, 7)))     // 탄수화물(g)
                        .sugar(parseFloat(getSafe(nextLine, 8)))     // 당류(g)
                        .calcium(parseFloat(getSafe(nextLine, 9)))   // 칼슘(mg)
                        .sodium(parseFloat(getSafe(nextLine, 10)))   // 나트륨(mg)
                        .cholesterol(parseFloat(getSafe(nextLine, 11))) // 콜레스테롤(mg)
                        .weight(parseFloat(getSafe(nextLine, 12)))    // 식품중량
                        .dataDate(LocalDate.parse(getSafe(nextLine, 13))) // 데이터기준일자 (YYYY-MM-DD)
                        .build();
                    foods.add(food);
                }
                foodRepository.saveAll(foods);
                return foods.size();
            }
        } catch (Exception e) {
            throw new RuntimeException("CSV upload error: " + e.getMessage(), e);
        }
    }

    private static boolean hasUtf8Bom(byte[] data) {
        return data.length >= 3 && (data[0] == (byte) 0xEF && data[1] == (byte) 0xBB && data[2] == (byte) 0xBF);
    }

    /**
     * Try to detect charset: prefer UTF-8 (with or without BOM); if decoding fails, fallback to MS949.
     */
    private static Charset detectCharset(byte[] data) {
        // If it has UTF-8 BOM, it's UTF-8
        if (hasUtf8Bom(data)) {
            return StandardCharsets.UTF_8;
        }
        // Try strict UTF-8 decode
        CharsetDecoder dec = StandardCharsets.UTF_8.newDecoder()
            .onMalformedInput(CodingErrorAction.REPORT)
            .onUnmappableCharacter(CodingErrorAction.REPORT);
        try {
            dec.decode(ByteBuffer.wrap(data));
            return StandardCharsets.UTF_8;
        } catch (Exception ignore) {
            // fall through to CP949
        }
        return Charset.forName("MS949");
    }

    private static String getSafe(String[] arr, int idx) {
        return idx < arr.length ? arr[idx] : null;
    }

    private Float parseFloat(String value) {
        try {
            if (value == null || value.isBlank()) return null;
            // "900 g" 같은 경우 숫자만 추출
            String numeric = value.replaceAll("[^0-9.]", "");
            if (numeric.isBlank()) return null;
            return Float.parseFloat(numeric);
        } catch (Exception e) {
            return null;
        }
    }

    public CursorResponse<FoodResponseDto.Summary> searchFoods(String keyword, UUID cursorId, int size) {
        Pageable pageable = PageRequest.of(0, size + 1);

        List<FoodEntity> foods;
        if (cursorId == null) {
            foods = foodRepository.findFirstPage(keyword, pageable);
        } else {
            foods = foodRepository.findByCursor(keyword, cursorId, pageable);
        }

        boolean hasNext = foods.size() > size;
        List<FoodEntity> resizedFoods = hasNext ? foods.subList(0, size) : foods;

        String nextCursor = hasNext ? resizedFoods.get(resizedFoods.size() - 1)
                                                  .getId()
                                                  .toString() : null;

        List<FoodResponseDto.Summary> result = resizedFoods.stream()
                                                           .map(FoodResponseDto.Summary::fromEntity)
                                                           .toList()
            ;

        return CursorResponse.<FoodResponseDto.Summary>builder()
                             .items(result)
                             .nextCursor(nextCursor)
                             .hasNext(hasNext)
                             .build();
    }

}
