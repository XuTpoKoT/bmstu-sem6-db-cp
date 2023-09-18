package com.music_shop.mpa.util;

import org.springframework.ui.Model;

import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

public class PaginationHelper {
    public static void addPaginationInfoToModel(int countItems, int page, int pageSize, Model model) {
        int countPages = (int) Math.ceil((double) countItems / pageSize);
        if (page < 1) {
            page = 1;
        }  else if (page > countPages) {
            page = countPages;
        }
        int startPage = Math.max(page - 1, 1);
        int endPage = Math.min(page + 1, countPages);
        List<Integer> pageNumbers = IntStream.rangeClosed(startPage, endPage)
                .boxed().toList();
        model.addAttribute("pageNumbers", pageNumbers);
        model.addAttribute("needDots", Collections.max(pageNumbers) + 1 < countPages);
        model.addAttribute("needLastPage", Collections.max(pageNumbers) < countPages);
        model.addAttribute("countPages", countPages);
        model.addAttribute("currentPage", page);
    }
}
