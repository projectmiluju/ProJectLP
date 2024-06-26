package com.example.ProJectLP.controller;

import com.example.ProJectLP.dto.request.VinylRequestDto;
import com.example.ProJectLP.service.VinylService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


@RequiredArgsConstructor
@RestController
public class VinylController {

    private final VinylService vinylService;

    //vinyl 등록
    @RequestMapping(value = "/upload/vinyl", method = RequestMethod.POST)
    public ResponseEntity<?> uploadVinyl(@RequestPart VinylRequestDto requestDto, @RequestPart MultipartFile multipartFile, HttpServletRequest request) throws IOException {
        return vinylService.uploadVinyl(requestDto, multipartFile, request);
    }

    //vinyl 삭제
    @RequestMapping(value = "/delete/vinyl/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteVinyl(@PathVariable Long id, HttpServletRequest request) {
        return vinylService.deleteVinyl(id,request);
    }

    //vinyl 업데이트
    @RequestMapping(value = "/update/vinyl/{id}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateVinyl(@PathVariable Long id, @RequestPart VinylRequestDto requestDto,
                                      @RequestPart MultipartFile multipartFile, HttpServletRequest request) throws IOException {
        return vinylService.updateVinyl(id,requestDto,multipartFile,request);
    }

    //vinyl 전체조회
    @RequestMapping(value = "/get/vinyllist", method = RequestMethod.GET)
    public ResponseEntity<?> getVinyls(@RequestParam("pageNum") int page, @RequestParam("pageLimit") int limit) {
        page = page -1;
        return vinylService.getVinylList(page,limit);
    }

    //vinyl 상세조회
    @RequestMapping(value = "/get/vinyl/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getVinyl(@PathVariable Long id, HttpServletRequest request, HttpServletResponse response) {
        vinylService.updateView(id, request, response);
        return vinylService.getVinyl(id);
    }

}
