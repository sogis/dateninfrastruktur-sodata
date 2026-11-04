package ch.so.agi.datenportal.catalog.domain;

import java.util.Objects;
import java.util.Map;

public record Theme(String identifier, String displayName) {

    private static final Map<String, String> DISPLAY_NAMES = Map.ofEntries(
            Map.entry("Arbeit_Erwerb", "Arbeit und Erwerb"),
            Map.entry("Bau_und_Wohnungswesen", "Bau- und Wohnungswesen"),
            Map.entry("Bevoelkerung", "Bevölkerung"),
            Map.entry("Bildung_Wissenschaft", "Bildung und Wissenschaft"),
            Map.entry("Energie", "Energie"),
            Map.entry("Finanzen", "Finanzen"),
            Map.entry("Geografie", "Geografie"),
            Map.entry("Gesetzgebung", "Gesetzgebung"),
            Map.entry("Gesundheit", "Gesundheit"),
            Map.entry("Handel", "Handel"),
            Map.entry("Industrie_und_Dienstleistungen", "Industrie und Dienstleistungen"),
            Map.entry("Kriminalitaet_Strafrecht", "Kriminalität und Strafrecht"),
            Map.entry("Kultur_Medien_Informationsgesellschaft_Sport", "Kultur, Medien, Informationsgesellschaft und Sport"),
            Map.entry("Landwirtschaft_Forstwirtschaft", "Landwirtschaft und Forstwirtschaft"),
            Map.entry("Mobilitaet_und_Verkehr", "Mobilität und Verkehr"),
            Map.entry("Oeffentliche_Ordnung_und_Sicherheit", "Öffentliche Ordnung und Sicherheit"),
            Map.entry("Politik", "Politik"),
            Map.entry("Preise", "Preise"),
            Map.entry("Raum_und_Umwelt", "Raum und Umwelt"),
            Map.entry("Soziale_Sicherheit", "Soziale Sicherheit"),
            Map.entry("Statistische_Grundlagen", "Statistische Grundlagen"),
            Map.entry("Tourismus", "Tourismus"),
            Map.entry("Verwaltung", "Verwaltung"),
            Map.entry("Volkswirtschaft", "Volkswirtschaft"));

    public static Theme fromModelValue(String code){return new Theme(code,DISPLAY_NAMES.getOrDefault(code,code.replace('_',' ')));}

    public Theme {
        Objects.requireNonNull(identifier, "identifier must not be null");
        Objects.requireNonNull(displayName, "displayName must not be null");
    }
}
