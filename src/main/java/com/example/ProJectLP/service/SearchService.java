package com.example.ProJectLP.service;

import com.example.ProJectLP.domain.vinyl.Vinyl;
import com.example.ProJectLP.domain.vinyl.VinylRepository;
import com.example.ProJectLP.dto.response.PageVinylResponseDto;
import com.example.ProJectLP.dto.response.ResponseDto;
import com.example.ProJectLP.dto.response.VinylListResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final VinylRepository vinylRepository;

    public ResponseDto<?> searchVinyl(String keyword, int page, int limit) {

        Sort.Direction direction = Sort.Direction.DESC;
        Sort sort = Sort.by(direction, "modifiedAt");
        Pageable pageable = PageRequest.of(page, limit, sort);

        Page<Vinyl> vinylList = vinylRepository.findByTitle(keyword, pageable);
        List<VinylListResponseDto> vinylListResponseDtoList = new ArrayList<>();
        for (Vinyl vinyl : vinylList) {
            vinylListResponseDtoList.add(new VinylListResponseDto(vinyl));
        }

        PageVinylResponseDto pageVinylResponseDto = PageVinylResponseDto.builder()
                .currPage(vinylList.getNumber()+1)
                .totalPage(vinylList.getTotalPages())
                .currContent(vinylList.getNumberOfElements())
                .vinylList(vinylListResponseDtoList).build();

        return ResponseDto.success(pageVinylResponseDto);
    }
}
