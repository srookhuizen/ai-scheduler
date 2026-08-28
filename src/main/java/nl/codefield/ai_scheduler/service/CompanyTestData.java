package nl.codefield.ai_scheduler.service;

import nl.codefield.ai_scheduler.model.Address;
import nl.codefield.ai_scheduler.model.Company;

import java.util.List;

public class CompanyTestData {

    public static List<Company> createMockCompanies() {
        return List.of(
                Company.builder()
                        .name("TechSolutions B.V.")
                        .kvkNumber("12345678")
                        .workRadius(25)
                        .address(Address.builder()
                                .street("Keizersgracht")
                                .number("421")
                                .postalCode("1016 EK")
                                .city("Amsterdam")
                                .build())
                        .build(),

                Company.builder()
                        .name("De Groene Bakkerij")
                        .kvkNumber("87654321")
                        .workRadius(10)
                        .address(Address.builder()
                                .street("Dorpsstraat")
                                .number("12 A")
                                .postalCode("3521 VS")
                                .city("Utrecht")
                                .build())
                        .build(),

                Company.builder()
                        .name("Logistiek & Co")
                        .kvkNumber("56781234")
                        .workRadius(150)
                        .address(Address.builder()
                                .street("Sluisweg")
                                .number("88")
                                .postalCode("3087 BA")
                                .city("Rotterdam")
                                .build())
                        .build(),

                Company.builder()
                        .name("Innovatie Hub")
                        .kvkNumber("43218765")
                        .workRadius(50)
                        .address(Address.builder()
                                .street("Stationsplein")
                                .number("5")
                                .postalCode("5611 AC")
                                .city("Eindhoven")
                                .build())
                        .build(),

                Company.builder()
                        .name("Jansen Consultancy")
                        .kvkNumber("98765432")
                        .workRadius(100)
                        .address(Address.builder()
                                .street("Markt")
                                .number("103")
                                .postalCode("7311 LG")
                                .city("Apeldoorn")
                                .build())
                        .build(),

                Company.builder()
                        .name("Bouwbedrijf Dijkstra")
                        .kvkNumber("24681357")
                        .workRadius(75)
                        .address(Address.builder()
                                .street("Brede Weg")
                                .number("14")
                                .postalCode("8911 AA")
                                .city("Leeuwarden")
                                .build())
                        .build(),

                Company.builder()
                        .name("Schoonmaak Service West")
                        .kvkNumber("13572468")
                        .workRadius(30)
                        .address(Address.builder()
                                .street("Zandstraat")
                                .number("202 B")
                                .postalCode("2513 AA")
                                .city("Den Haag")
                                .build())
                        .build(),

                Company.builder()
                        .name("Noord Tech")
                        .kvkNumber("35712468")
                        .workRadius(60)
                        .address(Address.builder()
                                .street("Grote Markt")
                                .number("1")
                                .postalCode("9712 HN")
                                .city("Groningen")
                                .build())
                        .build(),

                Company.builder()
                        .name("Elektra & Warmte")
                        .kvkNumber("86421357")
                        .workRadius(40)
                        .address(Address.builder()
                                .street("Industrieweg")
                                .number("45")
                                .postalCode("7547 SB")
                                .city("Enschede")
                                .build())
                        .build(),

                Company.builder()
                        .name("Limburg Media")
                        .kvkNumber("57134682")
                        .workRadius(120)
                        .address(Address.builder()
                                .street("Vrijthof")
                                .number("8")
                                .postalCode("6211 LD")
                                .city("Maastricht")
                                .build())
                        .build()
        );
    }
}
