package com.example.tourtravelserver.service.impl;

import com.example.tourtravelserver.dto.*;
import com.example.tourtravelserver.entity.*;
import com.example.tourtravelserver.enums.BookingStatus;
import com.example.tourtravelserver.enums.PaymentMethod;
import com.example.tourtravelserver.enums.PaymentStatus;
import com.example.tourtravelserver.repository.*;
import com.example.tourtravelserver.service.IBookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.example.tourtravelserver.util.ConvertImageUrl.convertImageUrlToBase64;

@Service
@RequiredArgsConstructor
public class BookingService implements IBookingService {
    private final IBookingRepository bookingRepository;
    private final IPaymentRepository paymentRepository;
    private final ITourRepository tourRepository;
    private final ITourScheduleRepository tourScheduleRepository;
    private final IUserRepository userRepository;
    private final IRoleRepository roleRepository;

    @Override
    public void createPendingTransaction(Long bookingId, String txnRef, long amount) {
        Optional<Booking> optionalBooking = bookingRepository.findById(bookingId);
        if (optionalBooking.isEmpty()) {
            throw new RuntimeException("Không tìm thấy booking với ID: " + bookingId);
        }

        Booking booking = optionalBooking.get();

        // Tạo bản ghi Payment mới
        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setTransactionCode(txnRef);
        payment.setAmount(BigDecimal.valueOf(amount));
        payment.setPaymentMethod(PaymentMethod.VN_PAY);
        payment.setStatus(PaymentStatus.PENDING);
        payment.setPaymentDate(LocalDateTime.now());

        paymentRepository.save(payment);

        booking.setStatus(BookingStatus.PENDING);
        bookingRepository.save(booking);
    }

    @Override
    public void markBookingAsPaid(String txnRef, long amount, Map<String, String> extraParams) {
        Optional<Payment> optionalPayment = paymentRepository.findByTransactionCode(txnRef);
        if (optionalPayment.isEmpty()) {
            throw new RuntimeException("Không tìm thấy giao dịch với mã: " + txnRef);
        }

        Payment payment = optionalPayment.get();
        Booking booking = payment.getBooking();

        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setPaymentDate(LocalDateTime.now());
        payment.setAmount(BigDecimal.valueOf(amount));
        paymentRepository.save(payment);

        booking.setStatus(BookingStatus.CONFIRMED); // hoặc SUCCESS, tùy enum bạn định nghĩa
        bookingRepository.save(booking);
    }

    @Override
    public void markBookingAsFailed(String txnRef, String responseCode, Map<String, String> extraParams) {
        Optional<Payment> optionalPayment = paymentRepository.findByTransactionCode(txnRef);
        if (optionalPayment.isEmpty()) {
            throw new RuntimeException("Không tìm thấy giao dịch với mã: " + txnRef);
        }

        Payment payment = optionalPayment.get();
        Booking booking = payment.getBooking();

        payment.setStatus(PaymentStatus.FAILED);
        paymentRepository.save(payment);

        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
    }

    @Override
    public Booking createBooking(BookingRequest request) {
        TourSchedule tourSchedule = tourScheduleRepository.findById(request.getTourScheduleId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy TourSchedule với ID: " + request.getTourScheduleId()));

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy User với email: " + email));

        // Xây dựng booking
        Booking booking = Booking.builder()
                .bookingDate(request.getBookingDate() != null ? request.getBookingDate() : LocalDateTime.now())
                .numberOfPeople(request.getNumberOfPeople())
                .adultCount(request.getAdultCount())
                .childCount(request.getChildCount())
                .babyCount(request.getBabyCount())
                .totalPrice(request.getTotalPrice() != null ? request.getTotalPrice() : BigDecimal.ZERO)
                .status(request.getStatus() != null ? BookingStatus.valueOf(request.getStatus()) : BookingStatus.PENDING)
                .tourSchedule(tourSchedule)
                .user(user)
                .build();

        return bookingRepository.save(booking);
    }

    @Override
    public Optional<Booking> findById(Long id) {
        Optional<Booking> optionalBooking = bookingRepository.findById(id);

        optionalBooking.ifPresent(booking -> {
            // Chuyển tour image
            Tour tour = booking.getTourSchedule().getTour();
            tour.setImage(convertImageUrlToBase64(tour.getImage()));

            // Chuyển tất cả ảnh activities
            if (tour.getItineraryDays() != null) {
                tour.getItineraryDays().forEach(day -> {
                    if (day.getActivities() != null) {
                        day.getActivities().forEach(act -> {
                            act.setImageUrl(convertImageUrlToBase64(act.getImageUrl()));
                        });
                    }
                });
            }
        });

        return optionalBooking;
    }

    @Override
    public List<Booking> findAll() {
        return bookingRepository.findAll();
    }

    @Override
    public Page<BookingResponse> findAllWithFilters(String userName, String bookingCode, String tourTitle, String status, Pageable pageable) {
        Page<BookingProjection> projections = bookingRepository.findAllWithFiltersProjection(
                userName, bookingCode, tourTitle, status, pageable
        );

        return projections.map(this::mapProjectionToResponse);
    }

    @Override
    public Page<BookingResponse> globalSearch(String search, String status, Pageable pageable) {
        Page<BookingProjection> projections = bookingRepository.findByGlobalSearch(
                search, status, pageable
        );

        return projections.map(this::mapProjectionToResponse);
    }


    @Override
    public AdminBookingResponse createBookingByAdmin(AdminBookingRequest request) {
        try {
            // Validate request
            Integer totalPeople = request.getTotalPeople();
            if (totalPeople == null || totalPeople <= 0) {
                throw new RuntimeException("❌ Tổng số người phải lớn hơn 0");
            }


            LocalDate startDate = LocalDate.now();
//            try {
//                startDate = LocalDate.parse(request.getStartDate());
//
//                if (startDate.isBefore(LocalDate.now())) {
//                    throw new RuntimeException("❌ Ngày khởi hành phải là ngày trong tương lai hoặc hôm nay");
//                }
//            } catch (Exception e) {
//                throw new RuntimeException("❌ Định dạng ngày không hợp lệ: " + request.getStartDate());
//            }


            // TÌM HOẶC TẠO USER/CUSTOMER
            User customer;
            Optional<User> existingUser = userRepository.findByEmail(request.getCustomerEmail());

            if (existingUser.isPresent()) {
                User user = existingUser.get();
                boolean needsUpdate = false;

                if (!request.getCustomerName().equals(user.getName())) {
                    user.setName(request.getCustomerName());
                    needsUpdate = true;
                }

                if (!request.getPhone().equals(user.getPhone())) {
                    user.setPhone(request.getPhone());
                    needsUpdate = true;
                }

                customer = needsUpdate ? userRepository.save(user) : user;
            } else {
                // Tạo user mới
                Role customerRole = roleRepository.findByName("USER")
                        .orElseGet(() -> roleRepository.save(new Role(null, "USER")));

                customer = User.builder()
                        .email(request.getCustomerEmail())
                        .name(request.getCustomerName())
                        .phone(request.getPhone())
                        .password(UUID.randomUUID().toString().substring(0, 12))
                        .role(customerRole)
                        .status(true)
                        .emailVerification(true)
//                        .createdAt(LocalDateTime.now())
                        .build();

                customer = userRepository.save(customer);
            }

            // TÌM TOUR SCHEDULE
            TourSchedule tourSchedule = tourScheduleRepository.findByTourIdAndStartDate(request.getTourId(), startDate)
                    .orElseThrow(() -> new RuntimeException("❌ Không tìm thấy lịch trình tour với ID: " + request.getTourId() + " và ngày khởi hành: " + request.getStartDate()));

            // Kiểm tra số chỗ trống
            if (tourSchedule.getAvailableSlots() < totalPeople) {
                throw new RuntimeException("❌ Không đủ chỗ trống. Chỉ còn " + tourSchedule.getAvailableSlots() + " chỗ");
            }

            // Tính toán giá
            BigDecimal adultPrice = tourSchedule.getPrice().multiply(BigDecimal.valueOf(request.getAdults()));
            BigDecimal childPrice = tourSchedule.getChildPrice().multiply(BigDecimal.valueOf(request.getChildren()));
            BigDecimal babyPrice = tourSchedule.getBabyPrice().multiply(BigDecimal.valueOf(request.getBabies()));
            BigDecimal totalPrice = adultPrice.add(childPrice).add(babyPrice);

            // Tạo booking - SỬA FIELD customer THAY VÌ user
            Booking booking = Booking.builder()
                    .user(customer)
                    .tourSchedule(tourSchedule)
                    .adultCount(request.getAdults())
                    .childCount(request.getChildren())
                    .babyCount(request.getBabies())
                    .numberOfPeople(totalPeople)
                    .totalPrice(totalPrice)
                    .bookingDate(LocalDateTime.now())
                    .status(BookingStatus.PENDING)
                    .notes(request.getNotes())
                    .build();

            Booking savedBooking = bookingRepository.save(booking);

            // Cập nhật số chỗ trống
            tourSchedule.setAvailableSlots(tourSchedule.getAvailableSlots() - totalPeople);
            tourScheduleRepository.save(tourSchedule);

            // Build response - THÊM ID cho customer
            UserResponse customerInfo = UserResponse.builder()
                    .name(customer.getName())
                    .email(customer.getEmail())
                    .phone(customer.getPhone())
                    .avatar(customer.getAvatar())
                    .build();

            TourInfo tourInfo = TourInfo.builder()
                    .id(tourSchedule.getTour().getId())
                    .title(tourSchedule.getTour().getTitle())
                    .destination(tourSchedule.getTour().getDestination())
                    .durationDays(Integer.valueOf(tourSchedule.getTour().getDuration()))
                    .imageUrl(
                            tourSchedule.getTour().getImages().isEmpty()
                                    ? null
                                    : tourSchedule.getTour().getImages().get(0).getImageUrl()
                    )
                    .startDate(tourSchedule.getStartDate().atStartOfDay())
                    .endDate(tourSchedule.getEndDate().atStartOfDay())
                    .price(tourSchedule.getPrice().doubleValue())
                    .childPrice(tourSchedule.getChildPrice().doubleValue())
                    .babyPrice(tourSchedule.getBabyPrice().doubleValue())
                    .availableSlots(tourSchedule.getAvailableSlots())
                    .build();

            BookingDetails bookingDetails = BookingDetails.builder()
                    .adultCount(savedBooking.getAdultCount())
                    .childCount(savedBooking.getChildCount())
                    .babyCount(savedBooking.getBabyCount())
                    .totalPeople(savedBooking.getNumberOfPeople())
                    .notes(savedBooking.getNotes())
                    .cancelReason(savedBooking.getCancelReason())
                    .build();

            PaymentInfo paymentInfo = PaymentInfo.builder()
                    .basePrice(tourSchedule.getPrice().doubleValue())
                    .adultPrice(adultPrice.doubleValue())
                    .childPrice(childPrice.doubleValue())
                    .babyPrice(babyPrice.doubleValue())
                    .totalPrice(totalPrice.doubleValue())
                    .paymentMethod("ADMIN_CREATED")
                    .paymentStatus("COMPLETED")
                    .paidAt(LocalDateTime.now())
                    .build();

            return AdminBookingResponse.builder()
                    .id(savedBooking.getId())
                    .bookingCode(savedBooking.getBookingCode())
                    .customer(customerInfo)
                    .tour(tourInfo)
                    .details(bookingDetails)
                    .payment(paymentInfo)
                    .status(savedBooking.getStatus().name())
                    .bookingDate(savedBooking.getBookingDate())
                    .createdAt(savedBooking.getBookingDate())
                    .updatedAt(savedBooking.getBookingDate())
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("❌ Lỗi tạo booking: " + e.getMessage());
        }
    }
//    public AdminBookingResponse createBookingByAdmin(AdminBookingRequest request) {
//        try {
//            if (!request.isValidPeopleCount()) {
//                throw new RuntimeException("❌ Tổng số người không khớp với số lượng từng loại");
//            }
//
//            User customer = new User();
//            customer.setId(customer.getId());
//            customer.setName(customer.getName());
//            customer.setEmail(customer.getEmail());
//            customer.setAvatar(customer.getAvatar());
//
//
//            TourSchedule tourSchedule = tourScheduleRepository.findById(request.getTourScheduleId())
//                    .orElseThrow(() -> new RuntimeException("❌ Không tìm thấy lịch trình tour với ID: " + request.getTourScheduleId()));
//
//
//            if (tourSchedule.getAvailableSlots() < request.getTotalPeople()) {
//                throw new RuntimeException("❌ Không đủ chỗ trống. Chỉ còn " + tourSchedule.getAvailableSlots() + " chỗ");
//            }
//
//            BigDecimal adultPrice = tourSchedule.getPrice().multiply(BigDecimal.valueOf(request.getAdultCount()));
//            BigDecimal childPrice = tourSchedule.getChildPrice().multiply(BigDecimal.valueOf(request.getChildCount()));
//            BigDecimal babyPrice = tourSchedule.getBabyPrice().multiply(BigDecimal.valueOf(request.getBabyCount()));
//            BigDecimal totalPrice = adultPrice.add(childPrice).add(babyPrice);
//
//            Booking booking = Booking.builder()
//                    .tourSchedule(tourSchedule)
//                    .adultCount(request.getAdultCount())
//                    .childCount(request.getChildCount())
//                    .babyCount(request.getBabyCount())
//                    .numberOfPeople(request.getTotalPeople())
//                    .totalPrice(totalPrice)
//                    .bookingDate(LocalDateTime.now())
//                    .status(BookingStatus.valueOf(request.getStatus()))
//                    .notes(request.getNotes())
//                    .build();
//            Booking savedBooking = bookingRepository.save(booking);
//
//            UserResponse customerInfo = UserResponse.builder()
//                    .name(customer.getName())
//                    .email(customer.getEmail())
//                    .phone(customer.getPhone())
//                    .avatar(customer.getAvatar())
//                    .build();
//
//            TourInfo tourInfo = TourInfo.builder()
//                    .id(tourSchedule.getId())
//                    .title(tourSchedule.getTour().getTitle())
//                    .destination(tourSchedule.getTour().getDestination())
//                    .durationDays(Integer.valueOf(tourSchedule.getTour().getDuration()))
//                    .imageUrl(
//                            tourSchedule.getTour().getImages().isEmpty()
//                                    ? null
//                                    : tourSchedule.getTour().getImages().get(0).getImageUrl()
//                    )
//                    .startDate(tourSchedule.getStartDate().atStartOfDay())
//                    .endDate(tourSchedule.getEndDate().atStartOfDay())
//                    .price(tourSchedule.getPrice().doubleValue())
//                    .childPrice(tourSchedule.getChildPrice().doubleValue())
//                    .babyPrice(tourSchedule.getBabyPrice().doubleValue())
//                    .availableSlots(tourSchedule.getAvailableSlots())
//                    .build();
//
//            BookingDetails bookingDetails = BookingDetails.builder()
//                    .adultCount(savedBooking.getAdultCount())
//                    .childCount(savedBooking.getChildCount())
//                    .babyCount(savedBooking.getBabyCount())
//                    .totalPeople(savedBooking.getNumberOfPeople())
//                    .notes(savedBooking.getNotes())
//                    .cancelReason(savedBooking.getCancelReason())
//                    .build();
//
//            PaymentInfo paymentInfo = PaymentInfo.builder()
//                    .basePrice(tourSchedule.getPrice().doubleValue())
//                    .adultPrice(adultPrice.doubleValue())
//                    .childPrice(childPrice.doubleValue())
//                    .babyPrice(babyPrice.doubleValue())
//                    .totalPrice(totalPrice.doubleValue())
//                    .paymentMethod("ADMIN_CREATED")
//                    .paymentStatus("COMPLETED")
//                    .build();
//
//            return AdminBookingResponse.builder()
//                    .id(savedBooking.getId())
//                    .bookingCode(savedBooking.getBookingCode())
//                    .customer(customer.)
//                    .tour(tourInfo)
//                    .details(bookingDetails)
//                    .payment(paymentInfo)
//                    .status(savedBooking.getStatus().name())
//                    .statusDescription(getStatusDescription(savedBooking.getStatus()))
//                    .bookingDate(savedBooking.getBookingDate())
//                    .createdAt(savedBooking.getCreatedAt())
//                    .updatedAt(savedBooking.getUpdatedAt())
//                    .build();
//
//
//        } catch (Exception e) {
//            throw new RuntimeException("❌ Lỗi tạo booking: " + e.getMessage());
//        }
//    }

    private BookingResponse mapProjectionToResponse(BookingProjection projection) {
        return BookingResponse.builder()
                .id(projection.getId())
                .bookingCode(projection.getBookingCode())
                .bookingDate(projection.getBookingDate())
                .numberOfPeople(projection.getNumberOfPeople())
                .adultCount(projection.getAdultCount())
                .childCount(projection.getChildCount())
                .babyCount(projection.getBabyCount())
                .totalPrice(projection.getTotalPrice())
                .status(projection.getStatus())
                .user(UserResponse.builder()
                        .name(projection.getUserName())
                        .email(projection.getUserEmail())
                        .phone(projection.getUserPhone())
                        .avatar(projection.getUserAvatar())
                        .build())
                .tourSchedule(TourScheduleResponse.builder()
                        .startDate(projection.getStartDate())
                        .endDate(projection.getEndDate())
                        .price(projection.getSchedulePrice())
                        .childPrice(projection.getChildPrice())
                        .babyPrice(projection.getBabyPrice())
                        .tour(TourResponse.builder()
                                .title(projection.getTourTitle())
                                .duration(projection.getTourDuration())
                                .image(projection.getTourImage())
                                .basePrice(projection.getTourBasePrice())
                                .destination(projection.getTourDestination())
                                .build())
                        .build())
                .build();
    }
}