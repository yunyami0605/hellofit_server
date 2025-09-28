package com.hellofit.hellofit_server.food;

import com.hellofit.hellofit_server.food.dto.FoodResponseDto;
import com.hellofit.hellofit_server.global.dto.CursorResponse;
import com.opencsv.CSVReader;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class FoodService {

    private final FoodRepository foodRepository;

    public int saveFoodsFromCsv(MultipartFile file) {
        try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream(), java.nio.charset.Charset.forName("MS949")))) {
            String[] nextLine;
            List<FoodEntity> foods = new ArrayList<>();
            reader.readNext(); // skip header

            while ((nextLine = reader.readNext()) != null) {
                FoodEntity food = FoodEntity.builder()
                    .foodCode(nextLine[0])         // 식품코드
                    .foodName(nextLine[1])         // 식품명
                    .category(nextLine[7])         // 식품대분류명
                    .repFoodName(nextLine[9])      // 대표식품명
                    .kcal(parseFloat(nextLine[17]))      // 에너지(kcal)
                    .protein(parseFloat(nextLine[19]))   // 단백질(g)
                    .fat(parseFloat(nextLine[20]))       // 지방(g)
                    .carbs(parseFloat(nextLine[22]))     // 탄수화물(g)
                    .sugar(parseFloat(nextLine[23]))     // 당류(g)
                    .calcium(parseFloat(nextLine[25]))   // 칼슘(mg)
                    .sodium(parseFloat(nextLine[29]))    // 나트륨(mg)
                    .cholesterol(parseFloat(nextLine[38])) // 콜레스테롤(mg)
                    .weight(parseFloat(nextLine[43]))    // 식품중량
                    .dataDate(LocalDate.parse(nextLine[48])) // 데이터기준일자
                    .build();
                foods.add(food);
            }
            foodRepository.saveAll(foods);
            return foods.size();
        } catch (Exception e) {
            throw new RuntimeException("CSV upload error: " + e.getMessage(), e);
        }
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
            .toList();

        return CursorResponse.<FoodResponseDto.Summary>builder()
            .items(result)
            .nextCursor(nextCursor)
            .hasNext(hasNext)
            .build();
    }

}
