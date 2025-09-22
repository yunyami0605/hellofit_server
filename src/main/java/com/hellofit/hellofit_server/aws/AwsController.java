package com.hellofit.hellofit_server.aws;

import com.hellofit.hellofit_server.aws.dto.AwsRequestDto;
import com.hellofit.hellofit_server.aws.dto.AwsResponseDto;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/aws")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class AwsController {
    private final AwsService awsService;

    @PostMapping("/presigned")
    public ResponseEntity<AwsResponseDto.GetS3PresignedPatchUrl> getPresignedUrl(@RequestBody AwsRequestDto.GetS3PresignedBody request) {

        return ResponseEntity.ok(awsService.generatePresignedUrl(request.getFileType()));
    }


}
