package com.fit.tourservice.services;

import com.fit.tourservice.dtos.request.TourFilterCriteriaRequest;
import com.fit.tourservice.dtos.TourDTO;
import com.fit.tourservice.dtos.TourFeatureDTO;
import com.fit.tourservice.dtos.TourTicketDTO;
import com.fit.tourservice.enums.Region;
import com.fit.tourservice.enums.TypeTour;
import com.fit.tourservice.events.EventProducer;
import com.fit.tourservice.models.Tour;
import com.fit.tourservice.models.TourFeature;
import com.fit.tourservice.repositories.r2dbc.TourFeatureRepository;
import com.fit.tourservice.repositories.r2dbc.TourRepository;
import com.fit.tourservice.repositories.r2dbc.TourTicketRepository;
import com.fit.tourservice.utils.Constant;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.*;

@Service
@Slf4j
public class TourService {
    @Autowired
    private TourRepository tourRepository;
    @Autowired
    private TourFeatureRepository tourFeatureRepository;

    @Autowired
    private EventProducer eventProducer;
    @Qualifier("gson")
    @Autowired
    private Gson gson;
    @Autowired
    private TourTicketRepository tourTicketRepository;

    public Mono<TourDTO> addTour(TourDTO tourDTO) {
        return Mono.just(tourDTO)
                .map(TourDTO::convertToEnity)
                .flatMap(tour -> tourRepository.save(tour))
                .map(TourDTO::convertToDTO);
    }

    // Xóa Tour theo ID
    public Mono<Void> deleteTour(Long tourId) {
        return tourRepository.findById(tourId)
                .flatMap(tour -> tourRepository.delete(tour));
    }

    public Mono<TourDTO> updateTour(TourDTO tourDTO, Long tourId) {
        return tourRepository.findById(tourId)
                .flatMap(existTour -> {
                    Tour updateTour = TourDTO.convertToEnity(tourDTO);
                    updateTour.setTourId(existTour.getTourId());
                    return tourRepository.save(updateTour);
                })
                .map(TourDTO::convertToDTO);
    }

    public Flux<TourDTO> getAllTours(int page, int size) {
        return tourRepository.findAll()
                .map(TourDTO::convertToDTO)
                .skip((long) (page - 1) * size)
                .take(size);
    }

    public Mono<Map<String, Object>> getToursByNameContainingIgnoreCase(String name, int page, int size) {
        int offset = (page - 1) * size;

        return tourRepository.countToursByNameContainingIgnoreCase(name) // Đếm tổng số phần tử
                .flatMap(totalElements -> {
                    int totalPages = (int) Math.ceil((double) totalElements / size);
                    return tourRepository.findToursByNameContainingIgnoreCaseWithPagination(name, size, offset) // Lấy dữ liệu phân trang
                            .flatMap(this::buildTourDTO)
                            .collectList()
                            .map(content -> {
                                Map<String, Object> response = new HashMap<>();
                                response.put("content", content);
                                response.put("totalElements", totalElements);
                                response.put("totalPages", totalPages);
                                response.put("size", size);
                                response.put("number", page);
                                return response;
                            });
                });
    }


    public Flux<TourDTO> getToursByDayBetween(LocalDate startDate, LocalDate endDate, int page, int size) {
        return tourRepository.findToursByDayBetween(startDate, endDate)
                .map(TourDTO::convertToDTO)
                .skip((long) (page - 1) * size)
                .take(size);
    }


    public Flux<TourDTO> getToursByPriceBetween(Double minPrice, Double maxPrice, int page, int size) {
        return tourRepository.findToursByPriceBetween(minPrice, maxPrice)
                .map(TourDTO::convertToDTO)
                .skip((long) (page - 1) * size)
                .take(size);
    }

    public Mono<Map<String, Object>> getToursByTypeTour(TypeTour typeTour, Region region, int page, int size) {
        int offset = (page - 1) * size;

        return tourRepository.countToursByTypeTour(typeTour, region) // Đếm tổng số phần tử
                .flatMap(totalElements -> {
                    int totalPages = (int) Math.ceil((double) totalElements / size); // Tính tổng số trang
                    return tourRepository.findToursByTypeTour(typeTour, region, size, offset) // Lấy dữ liệu theo trang
                            .flatMap(this::buildTourDTO) // Chuyển đổi sang DTO
                            .collectList() // Gộp thành danh sách
                            .map(content -> {
                                Map<String, Object> response = new HashMap<>();
                                response.put("content", content);
                                response.put("totalElements", totalElements);
                                response.put("totalPages", totalPages);
                                response.put("size", size);
                                response.put("number", page);
                                return response;
                            });
                });
    }


    //Lay DS  Tour con han va con cho trong
    public Flux<TourDTO> getAvailableTours(int page, int size) {
        return tourRepository.findAvailableTours()
                .map(TourDTO::convertToDTO)
                .skip((long) (page - 1) * size)
                .take(size);
    }


    //Gửi yêu cầu lay preference den RecommendationService
    public Mono<Void> requestPreferences(Long customerId) {
        // Tạo thông điệp yêu cầu tiêu chí từ TourService
        String requestMessage = gson.toJson(Map.of("customerId", customerId));
        // Trả về Mono<Void> để giữ cho việc gửi message bất đồng bộ
        return eventProducer.send(Constant.REQUEST_RECOMMENDATION_TOPIC, String.valueOf(customerId), requestMessage).then();
    }


    public Flux<TourDTO> findToursByIds(List<Long> tourIds) {
        return tourRepository.findByTourIdIn(tourIds)
                .flatMap(this::buildTourDTO);
    }

    public Mono<TourDTO> getTourById(Long ticketId) {
        return tourTicketRepository.findByTicketId(ticketId)
                .flatMap(ticket -> {
                    if (ticket == null || ticket.getTourId() == null) {
                        return Mono.error(new IllegalArgumentException("Invalid ticket or missing tourId"));
                    }
                    return tourFeatureRepository.findById(ticket.getTourId())
                            .flatMap(tourFeature ->
                                    tourRepository.findById(ticket.getTourId())
                                            .map(tour -> createTourDTO(tour, tourFeature, TourTicketDTO.convertToDTO(ticket)))
                            );
                })
                .switchIfEmpty(Mono.error(new NoSuchElementException("Ticket not found for ID: " + ticketId)))
                .onErrorResume(e -> {
                    log.error("Error fetching tour by ticket ID: {}", e.getMessage(), e);
                    return Mono.error(e); // Bạn có thể chọn cách xử lý lỗi khác
                });
    }


    private Flux<TourDTO> buildTourDTO(Tour tour) {
        return tourFeatureRepository.findTourFeatureByTourId(tour.getTourId())
                .flatMap(tourFeature ->
                        tourTicketRepository.findClosestTourTicketByTourId(tour.getTourId())
                                .map(TourTicketDTO::convertToDTO)
                                .defaultIfEmpty(new TourTicketDTO())
                                .map(closestTicket -> createTourDTO(tour, tourFeature, closestTicket))
                );
    }

    private TourDTO createTourDTO(Tour tour, TourFeature tourFeature, TourTicketDTO ticket) {
        TourDTO tourDTO = TourDTO.convertToDTO(tour);
        tourDTO.setTourFeatureDTO(TourFeatureDTO.convertToDTO(tourFeature));
        tourDTO.setTicketId(ticket.getTicketId());
        tourDTO.setDepartureDate(ticket.getDepartureDate());
        tourDTO.setDepartureLocation(ticket.getDepartureLocation());
        tourDTO.setAvailableSlot(ticket.getAvailableSlot());
        return tourDTO;
    }

    private Page<TourDTO> buildPage(List<TourDTO> tours, long totalElements, int page, int size) {
        int totalPages = (int) Math.ceil((double) totalElements / size);
        return new PageImpl<>(tours, PageRequest.of(page - 1, size), totalElements) {
            @Override
            public int getTotalPages() {
                return totalPages;
            }
        };
    }

    private Flux<TourDTO> fetchToursByFeatures(Flux<TourFeature> tourFeatures) {
        return tourFeatures.flatMap(feature ->
                tourRepository.findById(feature.getTourId())
                        .flatMapMany(this::buildTourDTO)
        );
    }

    //Filter cụ thể Tour
    public Mono<Page<TourDTO>> findToursByCriteria(TourFilterCriteriaRequest criteria, int page, int size) {
        int offset = (page - 1) * size;

        return tourRepository.countToursByCriteria(
                        criteria.getMaxCost(),
                        criteria.getMaxDuration(),
                        criteria.getDepartureLocation(),
                        criteria.getStartDate(),
                        criteria.getTypeTour(),
                        criteria.getAccommodationQuality(),
                        criteria.getRegion(),
                        criteria.getTransportationMode()
                )
                .flatMap(count -> fetchTours(criteria, size, offset)
                        .collectList()
                        .map(tourList -> buildPage(tourList, count, page, size))
                );
    }

    private Flux<TourDTO> fetchTours(TourFilterCriteriaRequest criteria, int size, int offset) {
        return tourRepository.findToursByCriteria(
                        criteria.getMaxCost() != null ? criteria.getMaxCost() : 0,
                        criteria.getMaxDuration() != null ? criteria.getMaxDuration() : 0,
                        criteria.getDepartureLocation(),
                        criteria.getStartDate(),
                        criteria.getTypeTour(),
                        criteria.getAccommodationQuality(),
                        criteria.getRegion(),
                        criteria.getTransportationMode(),
                        size,
                        offset
                )
                .flatMap(this::buildTourDTO);
    }

    //Sx theo giá
    public Mono<Page<TourDTO>> findToursByRegionOrderByPrice(Region region, int offset, int size, boolean isAscending) {
        Mono<Long> totalCount = tourFeatureRepository.countToursByRegionAndDepartureDateAfter(region);

        // Lấy danh sách các TourFeature theo thứ tự sắp xếp
        Flux<TourFeature> tourFeatures = isAscending
                ? tourFeatureRepository.findAllByRegionAndDepartureDateAfterOrderByPriceAsc(region, size, offset)
                : tourFeatureRepository.findAllByRegionAndDepartureDateAfterOrderByPriceDesc(region, size, offset);

        return totalCount.flatMap(count -> fetchToursByFeatures(tourFeatures)
                .collectList()  // Thu thập danh sách TourDTO
                .map(tourList -> {
                    // Sắp xếp lại theo giá
                    if (isAscending) {
                        tourList.sort(Comparator.comparingDouble(TourDTO::getPrice));
                    } else {
                        tourList.sort(Comparator.comparingDouble(TourDTO::getPrice).reversed());
                    }

                    // Tính tổng số trang
                    int totalPages = (int) Math.ceil((double) count / size);

                    // Trả về PageImpl với danh sách đã sắp xếp
                    return new PageImpl<TourDTO>(tourList, PageRequest.of(offset / size, size), count) {
                        @Override
                        public int getTotalPages() {
                            return totalPages;
                        }
                    };
                })
        );
    }

    //Lọc tour theo vùng miền
    public Mono<Page<TourDTO>> getTourByRegion(Region region, int offset, int size, boolean isAscending) {
        // Lấy tổng số phần tử trước
        Mono<Long> totalCount = tourFeatureRepository.countToursByRegionAndDepartureDateAfter(region);

        // Lấy các TourFeature với trang hiện tại
        Flux<TourFeature> tourFeatures = isAscending
                ? tourFeatureRepository.findAllByRegionAndStartDateAfter(region, size, offset)
                : tourFeatureRepository.findAllByRegionAndStartDateBefore(region, size, offset);

        return totalCount.flatMap(count ->
                fetchToursByFeatures(tourFeatures)
                        .collectList()  // Thu thập danh sách TourDTO
                        .map(tourList -> {
                            if (tourList != null && !tourList.isEmpty()) {
                                // Sắp xếp theo ngày bắt đầu (startDate) theo thứ tự tăng dần hoặc giảm dần
                                if (isAscending) {
                                    // Sắp xếp tăng dần
                                    tourList.sort(Comparator.comparing(tourDTO ->
                                            Optional.ofNullable(tourDTO.getTourFeatureDTO())
                                                    .map(TourFeatureDTO::getStartDate)
                                                    .orElse(LocalDate.MAX)));
                                } else {
                                    // Sắp xếp giảm dần
                                    tourList.sort(Comparator.comparing((TourDTO tourDTO) ->
                                            Optional.ofNullable(tourDTO.getTourFeatureDTO())
                                                    .map(TourFeatureDTO::getStartDate)
                                                    .orElse(LocalDate.MAX), Comparator.reverseOrder()));
                                }
                            }

                            // Tính số trang
                            int totalPages = (int) Math.ceil((double) count / size);

                            // Trả về PageImpl
                            return new PageImpl<TourDTO>(tourList, PageRequest.of(offset / size, size), count) {
                                @Override
                                public int getTotalPages() {
                                    return totalPages; // Trả về giá trị totalPages đã tính
                                }
                            };
                        })
        );
    }


    //Lọc theo ngày khởi hành
    public Mono<Page<TourDTO>> findToursByRegionOrderByDepartureDate(Region region, int offset, int size, boolean isAscending) {
        // Lấy tổng số phần tử trước
        Mono<Long> totalCount = tourFeatureRepository.countToursByRegionAndDepartureDateAfter(region);

        // Chọn query theo thứ tự sắp xếp
        Flux<TourFeature> tourFeatures = isAscending
                ? tourFeatureRepository.findAllByRegionAndDepartureDateAfterOrderByPriceAsc(region, size, offset)
                : tourFeatureRepository.findAllByRegionAndDepartureDateAfterOrderByPriceDesc(region, size, offset);

        return totalCount.flatMap(count ->
                fetchToursByFeatures(tourFeatures)
                        .collectList()
                        .map(tourList -> {
                            // Sắp xếp lại nếu cần thiết
                            tourList.forEach(tourDTO -> {
                                log.info("TourDTO Departure Date: " + tourDTO.getDepartureDate());
                            });

                            if (isAscending) {
                                tourList.sort(Comparator.comparing(TourDTO::getDepartureDate));
                            } else {
                                tourList.sort(Comparator.comparing(TourDTO::getDepartureDate).reversed());
                            }

                            tourList.forEach(tourDTO -> {
                                log.info("Sorted TourDTO Departure Date: " + tourDTO.getDepartureDate());
                            });

                            // Tính tổng số trang
                            int totalPages = (int) Math.ceil((double) count / size);

                            // Trả về PageImpl
                            return new PageImpl<TourDTO>(tourList, PageRequest.of(offset / size, size), count) {
                                @Override
                                public int getTotalPages() {
                                    return totalPages;
                                }
                            };
                        })
        );
    }

//    public Mono<Page<TourDTO>> findToursByRegionOrderByDepartureDate(Region region, int offset, int size, boolean isAscending) {
//        // Lấy tổng số phần tử trước
//        Mono<Long> totalCount = tourFeatureRepository.countToursByRegionAndDepartureDateAfter(region);
//
//        // Chọn query theo thứ tự sắp xếp
//        Flux<Tour> tours = isAscending
//                ? tourRepository.findToursWithEarliestDepartureByRegion(region, size, offset)
//                : tourRepository.findToursWithLatestDepartureByRegion(region, size, offset);
//
//        return totalCount.flatMap(count ->
//                tours.flatMap(tour ->
//                                tourFeatureRepository.findById(tour.getTourId())
//                                        .flatMap(tourFeature ->
//                                                tourTicketRepository.findClosestTourTicketByTourId(tour.getTourId())
//                                                        .map(TourTicketDTO::convertToDTO)
//                                                        .defaultIfEmpty(new TourTicketDTO())
//                                                        .map(closestTicket -> {
//                                                            TourDTO tourDTO = TourDTO.convertToDTO(tour);
//                                                            tourDTO.setTourFeatureDTO(TourFeatureDTO.convertToDTO(tourFeature));
//                                                            tourDTO.setDepartureDate(closestTicket.getDepartureDate());
//                                                            tourDTO.setDepartureLocation(closestTicket.getDepartureLocation());
//                                                            tourDTO.setAvailableSlot(closestTicket.getAvailableSlot());
//                                                            return tourDTO;
//                                                        })
//                                        )
//                        )
//                        .collectList()
//                        .map(tourList -> {
//                            // Sắp xếp lại nếu cần thiết
//                            tourList.forEach(tourDTO -> {
//                                log.info("TourDTO Departure Date: " + tourDTO.getDepartureDate());
//                            });
//
//                            if (isAscending) {
//                                tourList.sort(Comparator.comparing(TourDTO::getDepartureDate));
//                            } else {
//                                tourList.sort(Comparator.comparing(TourDTO::getDepartureDate).reversed());
//                            }
//
//                            tourList.forEach(tourDTO -> {
//                                log.info("Sorted TourDTO Departure Date: " + tourDTO.getDepartureDate());
//                            });
//
//                            // Tính tổng số trang
//                            int totalPages = (int) Math.ceil((double) count / size);
//
//                            // Trả về PageImpl
//                            return new PageImpl<TourDTO>(tourList, PageRequest.of(offset / size, size), count) {
//                                @Override
//                                public int getTotalPages() {
//                                    return totalPages;
//                                }
//                            };
//                        })
//        );
//    }
//    public Mono<Page<TourDTO>> getTourByRegion(Region region, int offset, int size, boolean isAscending) {
//        // Lấy tổng số phần tử trước
//        Mono<Long> totalCount = tourFeatureRepository.countToursByRegionAndDepartureDateAfter(region);
//
//        // Lấy các TourFeature với trang hiện tại
//        Flux<TourFeature> tourFeatures = isAscending
//                ? tourFeatureRepository.findAllByRegionAndStartDateAfter(region, size, offset)
//                : tourFeatureRepository.findAllByRegionAndStartDateBefore(region, size, offset);
//
//        return totalCount.flatMap(count -> {
//            // Lấy danh sách các tour cho trang hiện tại
//            return tourFeatures.concatMap(tourFeature ->
//                            tourRepository.findById(tourFeature.getTourId())
//                                    .flatMap(tour ->
//                                            tourTicketRepository.findClosestTourTicketByTourId(tour.getTourId())
//                                                    .map(TourTicketDTO::convertToDTO)
//                                                    .defaultIfEmpty(new TourTicketDTO())
//                                                    .map(closestTicket -> {
//                                                        // Tạo DTO cho tour
//                                                        TourDTO tourDTO = TourDTO.convertToDTO(tour);
//                                                        tourDTO.setTourFeatureDTO(TourFeatureDTO.convertToDTO(tourFeature));
//                                                        tourDTO.setDepartureDate(closestTicket.getDepartureDate());
//                                                        tourDTO.setDepartureLocation(closestTicket.getDepartureLocation());
//                                                        tourDTO.setAvailableSlot(closestTicket.getAvailableSlot());
//                                                        return tourDTO;
//                                                    })
//                                    )
//                    )
//                    .collectList()  // Thu thập kết quả vào Mono<List<TourDTO>>
//                    .map(tourList -> {
//                        if (tourList != null && !tourList.isEmpty()) {
//                            // Sắp xếp theo ngày bắt đầu (startDate) theo thứ tự tăng dần hoặc giảm dần
//                            if (isAscending) {
//                                // Sắp xếp tăng dần
//                                tourList.sort(Comparator.comparing(tourDTO ->
//                                        Optional.ofNullable(tourDTO.getTourFeatureDTO())
//                                                .map(TourFeatureDTO::getStartDate)
//                                                .orElse(LocalDate.MAX)));
//                            } else {
//                                tourList.sort(Comparator.comparing((TourDTO tourDTO) ->
//                                        Optional.ofNullable(tourDTO.getTourFeatureDTO())
//                                                .map(TourFeatureDTO::getStartDate)
//                                                .orElse(LocalDate.MAX), Comparator.reverseOrder()));
//                            }
//                        }
//
//                        // Tính số trang
//                        int totalPages = (int) Math.ceil((double) count / size);
//
//                        // Trả về PageImpl
//                        return new PageImpl<TourDTO>(tourList, PageRequest.of(offset / size, size), count) {
//                            @Override
//                            public int getTotalPages() {
//                                return totalPages; // Trả về giá trị totalPages đã tính
//                            }
//                        };
//                    });
//        });
//    }
//    public Mono<Page<TourDTO>> findToursByRegionOrderByPrice(Region region, int offset, int size, boolean isAscending) {
//        // Lấy tổng số phần tử trước
//        Mono<Long> totalCount = tourFeatureRepository.countToursByRegionAndDepartureDateAfter(region);
//
//        // Chọn query theo thứ tự sắp xếp
//        Flux<TourFeature> tourFeatures = isAscending
//                ? tourFeatureRepository.findAllByRegionAndDepartureDateAfterOrderByPriceAsc(region, size, offset)
//                : tourFeatureRepository.findAllByRegionAndDepartureDateAfterOrderByPriceDesc(region, size, offset);
//        return totalCount.flatMap(count ->
//                tourFeatures.flatMap(tourFeature ->
//                                tourRepository.findById(tourFeature.getTourId())
//                                        .flatMap(tour ->
//                                                tourTicketRepository.findClosestTourTicketByTourId(tour.getTourId())
//                                                        .map(TourTicketDTO::convertToDTO)
//                                                        .defaultIfEmpty(new TourTicketDTO())
//                                                        .map(closestTicket -> {
//                                                            TourDTO tourDTO = TourDTO.convertToDTO(tour);
//                                                            tourDTO.setTourFeatureDTO(TourFeatureDTO.convertToDTO(tourFeature));
//                                                            tourDTO.setDepartureDate(closestTicket.getDepartureDate());
//                                                            tourDTO.setDepartureLocation(closestTicket.getDepartureLocation());
//                                                            tourDTO.setAvailableSlot(closestTicket.getAvailableSlot());
//                                                            return tourDTO;
//                                                        })
//                                        )
//                        )
//                        .collectList()
//                        .map(tourList -> {
//                            // Nếu cần thiết, sắp xếp lại trong Java
//                            if (isAscending) {
//                                tourList.sort(Comparator.comparingDouble(TourDTO::getPrice));
//                            } else {
//                                tourList.sort(Comparator.comparingDouble(TourDTO::getPrice).reversed());
//                            }
//
//                            // Tính tổng số trang
//                            int totalPages = (int) Math.ceil((double) count / size);
//
//                            // Trả về PageImpl
//                            return new PageImpl<TourDTO>(tourList, PageRequest.of(offset / size, size), count) {
//                                @Override
//                                public int getTotalPages() {
//                                    return totalPages;
//                                }
//                            };
//                        })
//        );
//    }

    //    public Mono<Page<TourDTO>> findToursByCriteria(TourFilterCriteriaRequest criteria, int page, int size) {
//        int offset = (page - 1) * size;
//
//        // Đếm tổng số kết quả
//        Mono<Long> totalCount = tourRepository.countToursByCriteria(
//                criteria.getMaxCost(),
//                criteria.getMaxDuration(),
//                criteria.getDepartureLocation(),
//                criteria.getStartDate(),
//                criteria.getTypeTour(),
//                criteria.getAccommodationQuality(),
//                criteria.getRegion(),
//                criteria.getTransportationMode()
//        );
//
//        return totalCount.flatMap(count ->
//                tourRepository.findToursByCriteria(
//                                criteria.getMaxCost(),
//                                criteria.getMaxDuration(),
//                                criteria.getDepartureLocation(),
//                                criteria.getStartDate(),
//                                criteria.getTypeTour(),
//                                criteria.getAccommodationQuality(),
//                                criteria.getRegion(),
//                                criteria.getTransportationMode(),
//                                size,
//                                offset
//                        ).flatMap(tour ->
//                                // Lấy TourFeature
//                                tourFeatureRepository.findTourFeatureByTourId(tour.getTourId())
//                                        .flatMap(tourFeature ->
//                                                // Lấy TourTicket gần nhất
//                                                tourTicketRepository.findClosestTourTicketByTourId(tour.getTourId())
//                                                        .map(TourTicketDTO::convertToDTO)
//                                                        .defaultIfEmpty(new TourTicketDTO())
//                                                        .map(closestTicket -> {
//                                                            // Tạo DTO cho tour
//                                                            TourDTO tourDTO = TourDTO.convertToDTO(tour);
//                                                            tourDTO.setTourFeatureDTO(TourFeatureDTO.convertToDTO(tourFeature));
//                                                            tourDTO.setDepartureDate(closestTicket.getDepartureDate());
//                                                            tourDTO.setDepartureLocation(closestTicket.getDepartureLocation());
//                                                            tourDTO.setAvailableSlot(closestTicket.getAvailableSlot());
//                                                            return tourDTO;
//                                                        })
//                                        )
//                        ).collectList() // Thu thập kết quả vào Mono<List<TourDTO>>
//                        .map(tourList -> {
//                            // Tính số trang
//                            int totalPages = (int) Math.ceil((double) count / size);
//
//                            return new PageImpl<TourDTO>(tourList, PageRequest.of(offset / size, size), count) {
//                                @Override
//                                public int getTotalPages() {
//                                    return totalPages; // Trả về giá trị totalPages đã tính
//                                }
//                            };
//                        })
//        );
//    }


}
