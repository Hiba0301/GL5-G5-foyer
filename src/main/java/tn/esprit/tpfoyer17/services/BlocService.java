package tn.esprit.tpfoyer17.services;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import tn.esprit.tpfoyer17.entities.Bloc;
import tn.esprit.tpfoyer17.entities.Chambre;
import tn.esprit.tpfoyer17.repositories.BlocRepository;
import tn.esprit.tpfoyer17.repositories.ChambreRepository;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BlocService implements IBlocService{
    private static final Logger logger = LogManager.getLogger(BlocService.class);

    BlocRepository blocRepository;
    ChambreRepository chambreRepository;


    @Override
    public Bloc addBloc(Bloc bloc) {
        logger.info("Ajout du bloc : {}", bloc.getNomBloc());
        Bloc savedBloc = blocRepository.save(bloc);
        logger.debug("Bloc enregistre avec l'id {}", savedBloc.getIdBloc());
        return savedBloc;
    }

    @Override
    public List<Bloc> getAllBlocs() {
        logger.debug("Recherche de tous les blocs");
        List<Bloc> blocs = (List<Bloc>) blocRepository.findAll();
        logger.debug("{} bloc(s) trouve(s)", blocs.size());
        return blocs;
    }

    @Override
    public Bloc getBlocById(long idBloc) {
        logger.debug("Recherche du bloc id={}", idBloc);
        return blocRepository.findById(idBloc).orElseThrow(() -> {
            logger.error("Bloc introuvable : id={}", idBloc);
            return new NoSuchElementException("Bloc introuvable : id=" + idBloc);
        });
    }

    @Override
    public void deleteBloc(long idBloc) {
        logger.warn("Suppression du bloc id={}", idBloc);
        blocRepository.deleteById(idBloc);
    }

    @Override
    public Bloc updateBloc(Bloc bloc) {
        logger.info("Modification du bloc id={}", bloc.getIdBloc());
        Bloc updatedBloc = blocRepository.save(bloc);
        logger.debug("Bloc mis a jour avec l'id {}", updatedBloc.getIdBloc());
        return updatedBloc;
    }

    @Override
    public Bloc affecterChambresABloc(List<Long> numChambre, long idBloc) {
        logger.info("Affectation de {} chambre(s) au bloc id={}", numChambre.size(), idBloc);
        Bloc bloc = blocRepository.findById(idBloc).orElseThrow(() -> {
            logger.error("Bloc introuvable : id={}", idBloc);
            return new NoSuchElementException("Bloc introuvable : id=" + idBloc);
        });
        List<Chambre> chambres = (List<Chambre>) chambreRepository.findAllById(numChambre);
        for (Chambre chambre : chambres) {
            chambre.setBloc(bloc);
        }
        chambreRepository.saveAll(chambres);
        logger.debug("{} chambre(s) affectee(s) au bloc id={}", chambres.size(), idBloc);
        return bloc;
    }

}
