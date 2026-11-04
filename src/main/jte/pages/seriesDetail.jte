@import ch.so.agi.datenportal.web.view.SeriesDetailPageVm
@param SeriesDetailPageVm page
@param String requestAccessUrl = null
@param boolean databaseMode = false
@template.layouts.main(chrome = page.chrome(), databaseMode = databaseMode, content = @`
  <section class="dp-detail-page dp-series-detail-page">
    <div class="dp-container">
      @if(requestAccessUrl != null)
        <p><a class="dp-action-pill" href="${requestAccessUrl}">Datenzugang anfragen</a> · <a href="/access/me">Meine Zugänge</a></p>
      @endif
      @template.components.detailHero(
        title = page.title(),
        description = page.description(),
        titleBadgeLabel = ""
      )
      <p class="dp-series-issues-intro">Zu dieser Serie sind folgende Ausgaben verfügbar:</p>
      <div class="dp-detail-summary-main">
        @template.components.seriesIssues(issues = page.issues(), showTitle = false)
      </div>
    </div>
  </section>
`)
