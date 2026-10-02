package com.ibake.config;

import com.ibake.entity.*;
import com.ibake.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CityRepository cityRepository;
    private final CategoryRepository categoryRepository;
    private final OccasionRepository occasionRepository;
    private final ProductRepository productRepository;
    private final VendorRepository vendorRepository;
    private final VendorProductCityRepository vendorProductCityRepository;
    private final BannerRepository bannerRepository;
    private final CartRepository cartRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        seedUsers();
        seedCities();
        seedCategoriesAndOccasions();
        seedVendorsAndProducts();
    }

    private void seedUsers() {
        if (!userRepository.existsByEmail("superadmin@ibake.in")) {
            User superAdmin = User.builder()
                    .email("superadmin@ibake.in")
                    .password(passwordEncoder.encode("SuperAdmin@123"))
                    .fullName("Head of Platform")
                    .phone("9876543210")
                    .role(Role.SUPER_ADMIN)
                    .build();
            userRepository.save(superAdmin);
            log.info("Initialized default Super Admin: superadmin@ibake.in");
        }

        if (!userRepository.existsByEmail("admin@ibake.in")) {
            User admin = User.builder()
                    .email("admin@ibake.in")
                    .password(passwordEncoder.encode("Admin@123"))
                    .fullName("Mumbai Operations Admin")
                    .phone("9876543211")
                    .role(Role.ADMIN)
                    .build();
            userRepository.save(admin);
            log.info("Initialized default Admin: admin@ibake.in");
        }

        if (!userRepository.existsByEmail("customer@example.com")) {
            User customer = User.builder()
                    .email("customer@example.com")
                    .password(passwordEncoder.encode("Customer@123"))
                    .fullName("Priya Sharma")
                    .phone("9876543212")
                    .role(Role.CUSTOMER)
                    .build();
            User savedCustomer = userRepository.save(customer);

            Cart cart = Cart.builder()
                    .user(savedCustomer)
                    .build();
            cartRepository.save(cart);
            log.info("Initialized sample Customer: customer@example.com");
        }
    }

    private void seedCities() {
        if (cityRepository.count() == 0) {
            cityRepository.save(City.builder().name("Mumbai").state("Maharashtra").active(true).build());
            cityRepository.save(City.builder().name("Delhi NCR").state("Delhi").active(true).build());
            cityRepository.save(City.builder().name("Bengaluru").state("Karnataka").active(true).build());
            cityRepository.save(City.builder().name("Pune").state("Maharashtra").active(true).build());
            cityRepository.save(City.builder().name("Hyderabad").state("Telangana").active(true).build());
            log.info("Seeded 5 major Indian metropolitan delivery cities");
        }
    }

    private void seedCategoriesAndOccasions() {
        if (categoryRepository.count() == 0) {
            categoryRepository.save(Category.builder()
                    .name("Cakes")
                    .slug("cakes")
                    .description("Freshly baked gourmet cakes, eggless options & photo cakes")
                    .imageUrl("https://images.unsplash.com/photo-1578985545062-69928b1d9587?auto=format&fit=crop&w=600&q=80")
                    .build());

            categoryRepository.save(Category.builder()
                    .name("Chocolates")
                    .slug("chocolates")
                    .description("Artisan Belgian truffles, pralines and luxury assorted boxes")
                    .imageUrl("https://images.unsplash.com/photo-1549007994-cb92caebd54b?auto=format&fit=crop&w=600&q=80")
                    .build());

            categoryRepository.save(Category.builder()
                    .name("Flowers")
                    .slug("flowers")
                    .description("Handcrafted floral bouquets, red roses and exotic lilies")
                    .imageUrl("https://images.unsplash.com/photo-1561181286-d3fee7d55364?auto=format&fit=crop&w=600&q=80")
                    .build());

            log.info("Seeded core Categories: Cakes, Chocolates, Flowers");
        }

        if (occasionRepository.count() == 0) {
            occasionRepository.save(Occasion.builder().name("Birthday").slug("birthday").description("Celebrate birthdays with personalized sweet surprises").build());
            occasionRepository.save(Occasion.builder().name("Diwali").slug("diwali").description("Festive sweetness for India's festival of lights").build());
            occasionRepository.save(Occasion.builder().name("Anniversary").slug("anniversary").description("Romantic cakes and flowers for couples").build());
            occasionRepository.save(Occasion.builder().name("Valentine's Day").slug("valentines-day").description("Heart-shaped red velvet cakes & rose bouquets").build());
            log.info("Seeded core Occasions: Birthday, Diwali, Anniversary, Valentine's Day");
        }
    }

    private void seedVendorsAndProducts() {
        if (vendorRepository.count() == 0 && productRepository.count() == 0) {
            Vendor vendor1 = vendorRepository.save(Vendor.builder()
                    .name("The Royal Patisserie Bandra")
                    .contactEmail("bandra@royalpatisserie.in")
                    .contactPhone("9123456780")
                    .address("Hill Road, Bandra West, Mumbai 400050")
                    .active(true)
                    .build());

            Vendor vendor2 = vendorRepository.save(Vendor.builder()
                    .name("Sweet Delights Koramangala")
                    .contactEmail("hello@sweetdelights.in")
                    .contactPhone("9123456781")
                    .address("100ft Road, 4th Block Koramangala, Bengaluru 560034")
                    .active(true)
                    .build());

            Vendor vendor3 = vendorRepository.save(Vendor.builder()
                    .name("Artisan Bakes Connaught Place")
                    .contactEmail("orders@artisanbakes.in")
                    .contactPhone("9123456782")
                    .address("Inner Circle, CP, New Delhi 110001")
                    .active(true)
                    .build());

            Category cakeCategory = categoryRepository.findBySlug("cakes").orElse(null);
            Category chocoCategory = categoryRepository.findBySlug("chocolates").orElse(null);
            Occasion bday = occasionRepository.findBySlug("birthday").orElse(null);
            Occasion diwali = occasionRepository.findBySlug("diwali").orElse(null);

            City mumbai = cityRepository.findByNameIgnoreCase("Mumbai").orElse(null);
            City bengaluru = cityRepository.findByNameIgnoreCase("Bengaluru").orElse(null);
            City delhi = cityRepository.findByNameIgnoreCase("Delhi NCR").orElse(null);

            if (cakeCategory != null) {
                Product truffleCake = productRepository.save(Product.builder()
                        .name("Dutch Chocolate Truffle Cake")
                        .description("Rich dark Belgian chocolate ganache layered with moist sponge. 100% vegetarian.")
                        .imageUrl("https://images.unsplash.com/photo-1578985545062-69928b1d9587?auto=format&fit=crop&w=800&q=80")
                        .category(cakeCategory)
                        .occasion(bday)
                        .isVeg(true)
                        .weightInGrams(500)
                        .active(true)
                        .build());

                Product redVelvet = productRepository.save(Product.builder()
                        .name("Royal Red Velvet Cream Cheese Cake")
                        .description("Classic crimson sponge layered with silky imported cream cheese frosting.")
                        .imageUrl("https://images.unsplash.com/photo-1586788680434-30d324b2d46f?auto=format&fit=crop&w=800&q=80")
                        .category(cakeCategory)
                        .occasion(bday)
                        .isVeg(true)
                        .weightInGrams(1000)
                        .active(true)
                        .build());

                // Assign vendors to products in cities with price and availability
                if (mumbai != null) {
                    vendorProductCityRepository.save(VendorProductCity.builder()
                            .vendor(vendor1)
                            .product(truffleCake)
                            .city(mumbai)
                            .price(new BigDecimal("699.00"))
                            .available(true)
                            .stockQuantity(25)
                            .preparationTimeHours(3)
                            .build());

                    vendorProductCityRepository.save(VendorProductCity.builder()
                            .vendor(vendor1)
                            .product(redVelvet)
                            .city(mumbai)
                            .price(new BigDecimal("1199.00"))
                            .available(true)
                            .stockQuantity(15)
                            .preparationTimeHours(4)
                            .build());
                }

                if (bengaluru != null) {
                    vendorProductCityRepository.save(VendorProductCity.builder()
                            .vendor(vendor2)
                            .product(truffleCake)
                            .city(bengaluru)
                            .price(new BigDecimal("649.00"))
                            .available(true)
                            .stockQuantity(30)
                            .preparationTimeHours(4)
                            .build());
                }

                if (delhi != null) {
                    vendorProductCityRepository.save(VendorProductCity.builder()
                            .vendor(vendor3)
                            .product(truffleCake)
                            .city(delhi)
                            .price(new BigDecimal("729.00"))
                            .available(true)
                            .stockQuantity(20)
                            .preparationTimeHours(3)
                            .build());
                }
            }

            if (chocoCategory != null && mumbai != null) {
                Product artisanChoco = productRepository.save(Product.builder()
                        .name("Diwali Gourmet Chocolate Hamper")
                        .description("Handcrafted assorted roasted almond, hazelnut and sea salt truffles in gold box.")
                        .imageUrl("https://images.unsplash.com/photo-1549007994-cb92caebd54b?auto=format&fit=crop&w=800&q=80")
                        .category(chocoCategory)
                        .occasion(diwali)
                        .isVeg(true)
                        .weightInGrams(400)
                        .active(true)
                        .build());

                vendorProductCityRepository.save(VendorProductCity.builder()
                        .vendor(vendor1)
                        .product(artisanChoco)
                        .city(mumbai)
                        .price(new BigDecimal("899.00"))
                        .available(true)
                        .stockQuantity(50)
                        .preparationTimeHours(2)
                        .build());
            }

            // Seed sample promotional banner
            if (mumbai != null) {
                bannerRepository.save(Banner.builder()
                        .title("Diwali Sweet Celebration — 20% Off Festive Cakes")
                        .imageUrl("https://images.unsplash.com/photo-1509440159596-0249088772ff?auto=format&fit=crop&w=1200&q=80")
                        .redirectUrl("/products?occasionId=" + (diwali != null ? diwali.getId() : ""))
                        .city(mumbai)
                        .occasion(diwali)
                        .startDate(LocalDateTime.now().minusDays(2))
                        .endDate(LocalDateTime.now().plusDays(30))
                        .active(true)
                        .build());
            }

            log.info("Seeded initial vendors, products, and city-scoped availability mappings.");
        }
    }
}
