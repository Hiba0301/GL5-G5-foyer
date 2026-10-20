package tn.esprit.tpfoyer17.services;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import tn.esprit.tpfoyer17.entities.Chambre;
import tn.esprit.tpfoyer17.entities.enumerations.TypeChambre;
import tn.esprit.tpfoyer17.repositories.ChambreRepository;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@AllArgsConstructor
@Log4j2
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChambreService implements IChambreService{
    ChambreRepository chambreRepository;

    @Override
    public Chambre addChambre(Chambre chambre) {
        Chambre savedChambre = chambreRepository.save(chambre);
        log.info("Chambre created id={} numero={} type={}", savedChambre.getIdChambre(), savedChambre.getNumeroChambre(), savedChambre.getTypeChambre());
        return savedChambre;


    }
    @Override
    public List<Chambre> getAllChambres() {
        List<Chambre> chambres = (List<Chambre>) chambreRepository.findAll();
        log.debug("Retrieved all chambres count={}", chambres.size());
        return chambres;
    }
    @Override
    public Chambre getChambreById(long idChambre) {
        log.debug("Getting chambre id={}", idChambre);
        return chambreRepository.findById(idChambre)
                .orElseThrow(() -> {
                    log.warn("Chambre introuvable id={}", idChambre);
                    return new NoSuchElementException("Chambre introuvable avec l'id " + idChambre);
                });
    }
    @Override
    public void deleteChambre(long idChambre) {
        chambreRepository.deleteById(idChambre);
        log.info("Chambre deleted id={}", idChambre);
    }
    @Override
    public Chambre updateChambre(Chambre chambre) {
        long id = chambre.getIdChambre();
        Chambre existingChambre = chambreRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Chambre introuvable id={}", id);
                    return new NoSuchElementException("Chambre introuvable avec l'id " + id);
                });
        existingChambre.setNumeroChambre(chambre.getNumeroChambre());
        existingChambre.setTypeChambre(chambre.getTypeChambre());
        Chambre updatedChambre = chambreRepository.save(existingChambre);
        log.info("Chambre updated id={} numero={} type={}", updatedChambre.getIdChambre(), updatedChambre.getNumeroChambre(), updatedChambre.getTypeChambre());
        return updatedChambre;
    }

    @Override
    public List<Chambre> getChambresParNomUniversite(String nomUniversite) {
        List<Chambre> chambres = chambreRepository.findByBlocFoyerUniversiteNomUniversite(nomUniversite);
        log.info("Chambre search by universite nomUniversite={} results={}", nomUniversite, chambres.size());
        return chambres;
    }

    @Override
    public List<Chambre> getChambresParBlocEtTypeKeyWord(long idBloc, TypeChambre typeC) {
        log.debug("Searching chambres by bloc and type idBloc={} type={}", idBloc, typeC);
        List<Chambre> chambres = chambreRepository.findByBlocIdBlocAndTypeChambre(idBloc,typeC);
        log.info("Chambre search by bloc and type idBloc={} type={} results={}", idBloc, typeC, chambres.size());
        return chambres;
    }

    @Override
    public List<Chambre> getChambresParBlocEtTypeJPQL(long idBloc, TypeChambre typeC) {
        log.debug("Searching chambres by bloc and type JPQL idBloc={} type={}", idBloc, typeC);
        List<Chambre> chambres = chambreRepository.findByBlocIdBlocAndTypeChambreJPQL(idBloc,typeC);
        log.info("Chambre search by bloc and type JPQL idBloc={} type={} results={}", idBloc, typeC, chambres.size());
        return chambres;
    }

    @Override
    public List<Chambre> getChambresNonReserveParNomUniversiteEtTypeChambre(String nomUniversite, TypeChambre type) {
        List<Chambre> chambres = chambreRepository.getChambresNonReserveParNomUniversiteEtTypeChambre(nomUniversite,type);
        log.info("Chambre search non reserve nomUniversite={} type={} results={}", nomUniversite, type, chambres.size());
        return chambres;
    }


    @Scheduled(cron = "*/30 * * * * * ")
    public void getChambreNonReserver(){
        try {
            log.debug("Unreserved chambres count={}", chambreRepository.getChambresNonReserve().size());
        } catch (RuntimeException e) {
            log.error("Scheduled check of unreserved chambres failed", e);
        }
    }

}
