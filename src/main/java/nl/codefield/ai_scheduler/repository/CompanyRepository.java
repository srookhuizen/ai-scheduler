package nl.codefield.ai_scheduler.repository;

import nl.codefield.ai_scheduler.model.BarberType;
import nl.codefield.ai_scheduler.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
    String EARTH_RADIUS = "6371";

    List<Company> findByBarberType(BarberType barberType);

    Optional<Company> findByPublicId(String publicId);

    @Query(value = """
            SELECT DISTINCT c.*, 
                   (""" + EARTH_RADIUS + """
                    * acos(LEAST(1.0, GREATEST(-1.0, cos(radians(:lat)) * cos(radians(a.latitude)) * 
                   cos(radians(a.longitude) - radians(:lon)) + 
                   sin(radians(:lat)) * sin(radians(a.latitude)))))) AS distance 
            FROM company c 
            JOIN address a ON c.address_id = a.id 
            WHERE c.barber_type = :barberType 
              AND (
                (:barberType = 'SHOP' AND (""" + EARTH_RADIUS + """
                * acos(LEAST(1.0, GREATEST(-1.0, cos(radians(:lat)) * cos(radians(a.latitude)) * 
               cos(radians(a.longitude) - radians(:lon)) + 
               sin(radians(:lat)) * sin(radians(a.latitude)))))) <= :radius)
            OR 
            (:barberType = 'HOUSE_CALL' AND (""" + EARTH_RADIUS + """
                    * acos(LEAST(1.0, GREATEST(-1.0, cos(radians(:lat)) * cos(radians(a.latitude)) * 
                   cos(radians(a.longitude) - radians(:lon)) + 
                   sin(radians(:lat)) * sin(radians(a.latitude)))))) <= c.work_radius)
              )
            ORDER BY distance ASC
            """, nativeQuery = true)
    List<Company> findCompaniesWithinRadius(@Param("lat") Double lat,
                                            @Param("lon") Double lon,
                                            @Param("radius") Integer radiusInKm,
                                            @Param("barberType") String barberType);


}
