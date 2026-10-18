package tn.esprit.tpfoyer17.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tn.esprit.tpfoyer17.dto.ChambreDTO;
import tn.esprit.tpfoyer17.entities.Chambre;
import tn.esprit.tpfoyer17.entities.enumerations.TypeChambre;
import tn.esprit.tpfoyer17.services.IChambreService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ChambreController.class)
class ChambreControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IChambreService chambreService;

    @Test
    void testAddingChambreIgnoresDtoId() throws Exception {
        ChambreDTO request = new ChambreDTO(99L, 101L, TypeChambre.SIMPLE);
        Chambre response = createChambre(1L, 101L, TypeChambre.SIMPLE);

        when(chambreService.addChambre(any(Chambre.class))).thenReturn(response);

        mockMvc.perform(post("/api/chambres/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idChambre").value(1))
                .andExpect(jsonPath("$.numeroChambre").value(101))
                .andExpect(jsonPath("$.typeChambre").value("SIMPLE"))
                .andExpect(jsonPath("$.bloc").doesNotExist())
                .andExpect(jsonPath("$.reservations").doesNotExist());

        ArgumentCaptor<Chambre> captor = ArgumentCaptor.forClass(Chambre.class);
        verify(chambreService).addChambre(captor.capture());
        assertEquals(0L, captor.getValue().getIdChambre());
        assertEquals(101L, captor.getValue().getNumeroChambre());
        assertEquals(TypeChambre.SIMPLE, captor.getValue().getTypeChambre());
    }

    @Test
    void testUpdatingChambreKeepsDtoId() throws Exception {
        ChambreDTO request = new ChambreDTO(7L, 202L, TypeChambre.DOUBLE);
        Chambre response = createChambre(7L, 202L, TypeChambre.DOUBLE);

        when(chambreService.updateChambre(any(Chambre.class))).thenReturn(response);

        mockMvc.perform(put("/api/chambres/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idChambre").value(7))
                .andExpect(jsonPath("$.numeroChambre").value(202))
                .andExpect(jsonPath("$.typeChambre").value("DOUBLE"))
                .andExpect(jsonPath("$.bloc").doesNotExist())
                .andExpect(jsonPath("$.reservations").doesNotExist());

        ArgumentCaptor<Chambre> captor = ArgumentCaptor.forClass(Chambre.class);
        verify(chambreService).updateChambre(captor.capture());
        assertEquals(7L, captor.getValue().getIdChambre());
        assertEquals(202L, captor.getValue().getNumeroChambre());
        assertEquals(TypeChambre.DOUBLE, captor.getValue().getTypeChambre());
    }

    @Test
    void testGettingAllChambres() throws Exception {
        Chambre chambre = createChambre(1L, 101L, TypeChambre.SIMPLE);
        when(chambreService.getAllChambres()).thenReturn(List.of(chambre));

        mockMvc.perform(get("/api/chambres/getAll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idChambre").value(1))
                .andExpect(jsonPath("$[0].numeroChambre").value(101))
                .andExpect(jsonPath("$[0].typeChambre").value("SIMPLE"))
                .andExpect(jsonPath("$[0].bloc").doesNotExist())
                .andExpect(jsonPath("$[0].reservations").doesNotExist());

        verify(chambreService).getAllChambres();
    }

    @Test
    void testGettingChambre() throws Exception {
        Chambre chambre = createChambre(1L, 101L, TypeChambre.SIMPLE);
        when(chambreService.getChambreById(1L)).thenReturn(chambre);

        mockMvc.perform(get("/api/chambres/get")
                        .param("idChambre", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idChambre").value(1))
                .andExpect(jsonPath("$.numeroChambre").value(101))
                .andExpect(jsonPath("$.typeChambre").value("SIMPLE"))
                .andExpect(jsonPath("$.bloc").doesNotExist())
                .andExpect(jsonPath("$.reservations").doesNotExist());

        verify(chambreService).getChambreById(1L);
    }

    @Test
    void testDeletingChambre() throws Exception {
        doNothing().when(chambreService).deleteChambre(1L);

        mockMvc.perform(delete("/api/chambres/delete/1"))
                .andExpect(status().isOk());

        verify(chambreService).deleteChambre(1L);
    }

    @Test
    void testGettingChambresParNomUniversite() throws Exception {
        Chambre chambre = createChambre(1L, 101L, TypeChambre.SIMPLE);
        when(chambreService.getChambresParNomUniversite("Esprit"))
                .thenReturn(List.of(chambre));

        mockMvc.perform(get("/api/chambres/get-par-nom-universite")
                        .param("nomUniversite", "Esprit"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idChambre").value(1))
                .andExpect(jsonPath("$[0].numeroChambre").value(101))
                .andExpect(jsonPath("$[0].typeChambre").value("SIMPLE"))
                .andExpect(jsonPath("$[0].bloc").doesNotExist())
                .andExpect(jsonPath("$[0].reservations").doesNotExist());

        verify(chambreService).getChambresParNomUniversite("Esprit");
    }

    @Test
    void testGettingChambresParBlocEtTypeKeyWord() throws Exception {
        Chambre chambre = createChambre(1L, 101L, TypeChambre.SIMPLE);
        when(chambreService.getChambresParBlocEtTypeKeyWord(2L, TypeChambre.SIMPLE))
                .thenReturn(List.of(chambre));

        mockMvc.perform(get("/api/chambres/get-par-bloc-type-keyword")
                        .param("idBloc", "2")
                        .param("typeC", "SIMPLE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idChambre").value(1))
                .andExpect(jsonPath("$[0].numeroChambre").value(101))
                .andExpect(jsonPath("$[0].typeChambre").value("SIMPLE"))
                .andExpect(jsonPath("$[0].bloc").doesNotExist())
                .andExpect(jsonPath("$[0].reservations").doesNotExist());

        verify(chambreService).getChambresParBlocEtTypeKeyWord(2L, TypeChambre.SIMPLE);
    }

    @Test
    void testGettingChambresParBlocEtTypeJpql() throws Exception {
        Chambre chambre = createChambre(1L, 101L, TypeChambre.SIMPLE);
        when(chambreService.getChambresParBlocEtTypeJPQL(2L, TypeChambre.SIMPLE))
                .thenReturn(List.of(chambre));

        mockMvc.perform(get("/api/chambres/get-par-bloc-type-jpql")
                        .param("idBloc", "2")
                        .param("typeC", "SIMPLE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idChambre").value(1))
                .andExpect(jsonPath("$[0].numeroChambre").value(101))
                .andExpect(jsonPath("$[0].typeChambre").value("SIMPLE"))
                .andExpect(jsonPath("$[0].bloc").doesNotExist())
                .andExpect(jsonPath("$[0].reservations").doesNotExist());

        verify(chambreService).getChambresParBlocEtTypeJPQL(2L, TypeChambre.SIMPLE);
    }

    @Test
    void testGettingNonReservedChambres() throws Exception {
        Chambre chambre = createChambre(1L, 101L, TypeChambre.SIMPLE);
        when(chambreService.getChambresNonReserveParNomUniversiteEtTypeChambre(
                "Esprit", TypeChambre.SIMPLE))
                .thenReturn(List.of(chambre));

        mockMvc.perform(get("/api/chambres/get-non-reserve-par-nom-universite-typechambre")
                        .param("nomUniversite", "Esprit")
                        .param("type", "SIMPLE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idChambre").value(1))
                .andExpect(jsonPath("$[0].numeroChambre").value(101))
                .andExpect(jsonPath("$[0].typeChambre").value("SIMPLE"))
                .andExpect(jsonPath("$[0].bloc").doesNotExist())
                .andExpect(jsonPath("$[0].reservations").doesNotExist());

        verify(chambreService).getChambresNonReserveParNomUniversiteEtTypeChambre(
                "Esprit", TypeChambre.SIMPLE);
    }

    private Chambre createChambre(long id, long numero, TypeChambre type) {
        return Chambre.builder()
                .idChambre(id)
                .numeroChambre(numero)
                .typeChambre(type)
                .build();
    }
}
