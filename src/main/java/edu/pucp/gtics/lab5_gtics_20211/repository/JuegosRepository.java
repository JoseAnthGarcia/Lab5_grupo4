package edu.pucp.gtics.lab5_gtics_20211.repository;

import edu.pucp.gtics.lab5_gtics_20211.entity.Juegos;
import edu.pucp.gtics.lab5_gtics_20211.entity.JuegosUserDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository

public interface JuegosRepository extends JpaRepository<Juegos,Integer> {
    @Query(value = "select * from juegors order by precio asc ",
            nativeQuery = true)
    List<Juegos> listarJuegos();

    @Query(value = "SELECT ju.image as image, ju.nombre as nombre, ju.descripcion as descripcion FROM juegosxusuario j, juegos ju\n" +
            "WHERE j.idjuego = ju.idjuego AND j.idusuario = ?1;",
            nativeQuery = true)
    List<JuegosUserDto> obtenerJuegosPorUser(Integer idUsuario);
}
