package org.example.service;

import fr.cnrs.lacito.liftapi.LiftDictionary;
import fr.cnrs.lacito.liftapi.xml.LiftWriter;

import java.nio.file.Path;

public class LiftService {

    public LiftDictionary load(Path path) throws Exception {
        // IMPORTANT : chargement tolérant (pas SAX)
        return LiftDictionary.loadDictionaryWithFile(path.toFile());
    }

    public void save(LiftDictionary dict, Path outPath) throws Exception {
        LiftWriter writer = new LiftWriter(outPath.toFile());
        writer.marshall(dict);
    }
}
