package org.lite.quotes.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/api/quotes")
@Tag(name = "Quotes", description = "APIs for managing quotes information")
public class QuotesController {
    // Ready for future quote-related endpoints
}
