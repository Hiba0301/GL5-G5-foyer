package tn.esprit.tpfoyer17.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import tn.esprit.tpfoyer17.dto.BlocDTO;
import tn.esprit.tpfoyer17.dto.BlocMapper;
import tn.esprit.tpfoyer17.services.IBlocService;

import java.util.List;

@RestController
@AllArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequestMapping("api/blocs")
@Tag(name = "Gestion des Blocs",description = "les apis pour gérer tout les Bloc")
public class BlocController {
    IBlocService blocService;

    // Order 1
    @Operation(description = "methode pour ajouter des blocs",summary = "ajouter un bloc",operationId = "1")
    @PostMapping("add")
    public BlocDTO addingBloc(@RequestBody BlocDTO blocDTO){
        return BlocMapper.toDTO(blocService.addBloc(BlocMapper.toEntity(blocDTO)));
    }

    // Order 2
    @GetMapping("getAll")
    public List<BlocDTO> gettingAllBloc(){
        return blocService.getAllBlocs().stream().map(BlocMapper::toDTO).toList();
    }

    // Order 3
    @GetMapping("get")
    public BlocDTO gettingBloc(@RequestParam("idBloc") long idBloc){
        return BlocMapper.toDTO(blocService.getBlocById(idBloc));
    }

    // Order 4
    @DeleteMapping("delete/{idBloc}")
    public void deletingBloc(@PathVariable("idBloc") long idBloc){
        blocService.deleteBloc(idBloc);
    }

    // Order 5
    @PutMapping("update")
    public BlocDTO updatingBloc(@RequestBody BlocDTO blocDTO){
        return BlocMapper.toDTO(blocService.updateBloc(BlocMapper.toEntity(blocDTO)));
    }

    // Order 6
    @PutMapping("affecter-chambre-bloc")
    public BlocDTO affecterChambresABloc(@RequestBody List<Long> numChambre,@RequestParam("idBloc") long idBloc){
        return BlocMapper.toDTO(blocService.affecterChambresABloc(numChambre,idBloc));
    }
}