package ch.so.agi.datenportal.web;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
public final class CatalogErrorControllerAdvice {

    private final ErrorPageVmFactory errorPageVmFactory;

    public CatalogErrorControllerAdvice(ErrorPageVmFactory errorPageVmFactory) {
        this.errorPageVmFactory = errorPageVmFactory;
    }

    @ExceptionHandler(CatalogNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String notFound(CatalogNotFoundException exception, Model model) {
        model.addAttribute("page", errorPageVmFactory.notFound(
                "Der angeforderte Katalogeintrag konnte nicht gefunden werden."));
        return "pages/notFound";
    }

    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String staticResourceNotFound(NoResourceFoundException exception, Model model) {
        model.addAttribute("page", errorPageVmFactory.notFound(
                "Die angeforderte Seite konnte nicht gefunden werden."));
        return "pages/notFound";
    }

    @ExceptionHandler(ch.so.agi.datenportal.catalog.database.CatalogDatabaseUnavailableException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public String databaseUnavailable(Model model){model.addAttribute("page",errorPageVmFactory.serviceUnavailable("Der Katalog ist momentan nicht verfügbar. Bitte versuchen Sie es später erneut."));return "pages/error";}

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String invalidQuery(Model model){model.addAttribute("page",errorPageVmFactory.notFound("Die Katalogabfrage ist ungültig."));return "pages/notFound";}
}
