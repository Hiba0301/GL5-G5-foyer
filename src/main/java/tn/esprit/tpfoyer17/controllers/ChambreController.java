package tn.esprit.tpfoyer17.controllers;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;
import tn.esprit.tpfoyer17.dto.ChambreDTO;
import tn.esprit.tpfoyer17.entities.Chambre;
import tn.esprit.tpfoyer17.entities.enumerations.TypeChambre;
import tn.esprit.tpfoyer17.services.IChambreService;

import java.util.List;

@RestController
@AllArgsConstructor
@Log4j2
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequestMapping("api/chambres")
public class ChambreController {
    IChambreService chambreService;

    @PostMapping("add")
    public Chambre addingChambre(@RequestBody ChambreDTO chambreDTO){
        log.info("Adding chambre");
        return chambreService.addChambre(toEntity(chambreDTO, false));
    }

    @GetMapping("getAll")
    public List<Chambre> gettingAllChambre(){
        log.info("Getting all chambres");
        return chambreService.getAllChambres();
    }

    @GetMapping("get")
    public Chambre gettingChambre(@RequestParam("idChambre") long idChambre){
        log.info("Getting chambre id={}", idChambre);
        return chambreService.getChambreById(idChambre);
    }

    @DeleteMapping("delete/{idChambre}")
    public void deletingChambre(@PathVariable("idChambre") long idChambre){
        log.info("Deleting chambre id={}", idChambre);
        chambreService.deleteChambre(idChambre);
    }

    @PutMapping("update")
    public Chambre updatingChambre(@RequestBody ChambreDTO chambreDTO){
        log.info("Updating chambre id={}", chambreDTO.getIdChambre());
        return chambreService.updateChambre(toEntity(chambreDTO, true));
    }

    @GetMapping("get-par-nom-universite")
    public List<Chambre> getChambresParNomUniversite(
            @RequestParam("nomUniversite") String nomUniversite) {
        log.info("Searching chambres nomUniversite={}", nomUniversite);
        return chambreService.getChambresParNomUniversite(nomUniversite);

    }
    @GetMapping("get-par-bloc-type-keyword")
    public List<Chambre> getChambresParBlocEtTypeKeyWord(@RequestParam("idBloc") long idBloc,@RequestParam("typeC") TypeChambre typeC) {
        log.info("Searching chambres idBloc={} type={}", idBloc, typeC);
        return chambreService.getChambresParBlocEtTypeKeyWord(idBloc,typeC);
    }
    @GetMapping("get-par-bloc-type-jpql")
    public List<Chambre> getChambresParBlocEtTypeJPQL(@RequestParam("idBloc") long idBloc,@RequestParam("typeC") TypeChambre typeC) {
        log.info("Searching chambres JPQL idBloc={} type={}", idBloc, typeC);
        return chambreService.getChambresParBlocEtTypeJPQL(idBloc,typeC);
    }
    @GetMapping("get-non-reserve-par-nom-universite-typechambre")
    public List<Chambre>  getChambresNonReserveParNomUniversiteEtTypeChambre( @RequestParam("nomUniversite") String nomUniversite,@RequestParam("type") TypeChambre type) {
        log.info("Searching non-reserved chambres nomUniversite={} type={}", nomUniversite, type);
        return chambreService.getChambresNonReserveParNomUniversiteEtTypeChambre(nomUniversite,type);
    }

    private Chambre toEntity(ChambreDTO chambreDTO, boolean withId) {
        Chambre.ChambreBuilder builder = Chambre.builder()
                .numeroChambre(chambreDTO.getNumeroChambre())
                .typeChambre(chambreDTO.getTypeChambre());

        if (withId) {
            builder.idChambre(chambreDTO.getIdChambre());
        }

        return builder.build();
    }
}
