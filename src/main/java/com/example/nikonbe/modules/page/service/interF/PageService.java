package com.example.nikonbe.modules.page.service.interF;

import com.example.nikonbe.modules.page.dto.request.PageCreateDto;
import com.example.nikonbe.modules.page.dto.request.PageUpdateDto;
import com.example.nikonbe.modules.page.dto.response.PageAdminDto;
import com.example.nikonbe.modules.page.dto.response.PageDto;

public interface PageService {

  PageAdminDto create(PageCreateDto dto);

  PageAdminDto update(Long id, PageUpdateDto dto);

  PageAdminDto getByPageKey(String pageKey);

  PageDto getBySlugForClient(String slug);
}
