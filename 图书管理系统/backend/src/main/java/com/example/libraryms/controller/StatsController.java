package com.example.libraryms.controller;

import com.example.libraryms.common.ApiResponse;
import com.example.libraryms.dto.stats.OverviewStats;
import com.example.libraryms.mapper.StatsMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stats")
public class StatsController {
  private final StatsMapper statsMapper;

  public StatsController(StatsMapper statsMapper) {
    this.statsMapper = statsMapper;
  }

  @GetMapping("/overview")
  public ApiResponse<OverviewStats> overview() {
    long totalBooks = statsMapper.countBooks(null);
    long enabledBooks = statsMapper.countBooks("ENABLED");
    long availableBooks = statsMapper.sumAvailableBooks();
    long activeReaders = statsMapper.countReaders("ACTIVE");
    long borrowedCount = statsMapper.countBorrowRecords("BORROWED");
    long totalBorrowRecords = statsMapper.countBorrowRecords(null);
    return ApiResponse.ok(new OverviewStats(
        totalBooks, enabledBooks, availableBooks, activeReaders, borrowedCount, totalBorrowRecords
    ));
  }
}

