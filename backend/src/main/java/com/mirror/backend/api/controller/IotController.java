package com.mirror.backend.api.controller;


import com.mirror.backend.api.dto.EmotionDto;
import com.mirror.backend.api.dto.IotRequestUserDto;
import com.mirror.backend.api.dto.IotResponseUserDto;
import com.mirror.backend.api.dto.chatbotDtos.ResponseFirstMirrorTextDto;
import com.mirror.backend.api.dto.chatbotDtos.ResponseSummaryScheduleDto;
import com.mirror.backend.api.service.EmotionService;
import com.mirror.backend.api.service.IotService;
import com.mirror.backend.common.utils.ApiResponse;
import com.mirror.backend.common.utils.ApiUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static com.mirror.backend.common.utils.ApiUtils.success;


@RequestMapping("/api/iot")
@RestController
@Tag(name = "iot", description = "IOT전용")
public class IotController {

    private IotService iotService;

    @Autowired
    private EmotionService emotionService;

    @Autowired
    IotController(IotService iotService){
        this.iotService = iotService;
    }

    @PostMapping("/users")
    @Operation(summary = "모든 유저 조회", description = "한 가정 내의 모든 유저 정보를 조회합니다.")
    public ApiUtils.ApiResult<List<IotResponseUserDto>> getProfileImage(@RequestBody IotRequestUserDto iotRequestUsersDto) {

        String mirrorId = iotRequestUsersDto.getMirrorId();
        List<IotResponseUserDto> users = iotService.findUsersInfo(mirrorId);

        return success( users);
    }

    @GetMapping("/calendar/summary")
    @Operation(summary = "하루 일정 요약 조회", description = "한 유저의 하루 일정 요약 TEXT를 조회합니다.")
    public ApiUtils.ApiResult<ResponseSummaryScheduleDto> getSummerySchedule(String userEmail){

        ResponseSummaryScheduleDto summaryScheduleTextDto = iotService.getSummerySchedule(userEmail);
        if ( summaryScheduleTextDto == null)
            return ApiUtils.success(null);

        return ApiUtils.success(summaryScheduleTextDto);
    }

    @GetMapping("/text/first")
    @Operation(summary = "유저 만남시 최초 TEXT 조회", description = "한 유저의 하루 중 최초 만남시의 TEXT를 조회합니다. ")
    public ApiUtils.ApiResult<ResponseFirstMirrorTextDto> getFirstMirrorText(String userEmail){

        ResponseFirstMirrorTextDto responseFirstMirrorTextDto = iotService.getFirstMirrorTextDto(userEmail);
        if ( responseFirstMirrorTextDto == null)
            return ApiUtils.success(null);

        return ApiUtils.success(responseFirstMirrorTextDto);
    }

    @PostMapping
    @Operation(summary = "오늘 감정 저장", description = "iot와 통신하여 오늘의 감정을 저장합니다.")
    public ApiUtils.ApiResult<Long> postEmotion(@RequestBody @Valid EmotionDto.EmotionReq emotionReq) {

        return ApiUtils.success(emotionService.saveEmotion(emotionReq));
    }
}
