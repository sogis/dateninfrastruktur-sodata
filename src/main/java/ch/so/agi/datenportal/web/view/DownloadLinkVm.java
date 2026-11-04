package ch.so.agi.datenportal.web.view;
/** A managed link is checked by the gateway on every request; this flag conveys no personal right. */
public record DownloadLinkVm(String label,String href,String ariaLabel,boolean gatewayProtected){
    public DownloadLinkVm(String label,String href,String ariaLabel){this(label,href,ariaLabel,false);}
}
