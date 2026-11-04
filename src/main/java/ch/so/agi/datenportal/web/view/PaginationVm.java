package ch.so.agi.datenportal.web.view;
/** Presentation of the bounded DB result window; legacy unpaged results have no controls. */
public record PaginationVm(int page,int totalPages,String previousHref,String nextHref){
    public static PaginationVm none(){return new PaginationVm(1,1,null,null);}
    public boolean visible(){return totalPages>1;}
}
