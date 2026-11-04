@import ch.so.agi.datenportal.web.view.EntryDetailPageVm
@param EntryDetailPageVm page
@param String requestAccessUrl = null
@param boolean databaseMode = false
@template.layouts.main(chrome = page.chrome(), databaseMode = databaseMode, content = @`
  <section class="dp-detail-page${page.seriesTitle().isPresent() ? " dp-issue-detail-page" : " dp-dataset-detail-page"}">
    <div class="dp-container">
      @if(requestAccessUrl != null)
        <p><a class="dp-action-pill" href="${requestAccessUrl}">Datenzugang anfragen</a> · <a href="/access/me">Meine Zugänge</a></p>
      @endif
      @if(page.seriesTitle().isPresent())
        <p class="dp-detail-kicker">
          Datenreihe:
          <a href="${page.seriesHref().get()}">${page.seriesTitle().get()}</a>
        </p>
      @endif
      @template.components.detailHero(
        title = page.title(),
        description = page.description(),
        titleBadgeLabel = page.currentIssue() ? "Aktuelle Ausgabe" : ""
      )
      <div class="dp-detail-summary-layout">
        <div class="dp-detail-summary-main">
          @template.components.detailFeatureDownloadStrip(
            features = page.features(),
            accessState = page.accessState(),
            downloads = page.downloads()
          )
          @template.components.metadataSection(section = page.overview())
          @if(!page.temporalCoverage().items().isEmpty())
            @template.components.metadataSection(section = page.temporalCoverage())
          @endif
          @if(!page.topics().items().isEmpty())
            @template.components.metadataSection(section = page.topics())
          @endif
          @template.components.metadataSection(section = page.responsibilitiesContact())
          @if(page.seriesTitle().isPresent())
            @template.components.relatedIssuesCard(relatedIssues = page.relatedIssues())
          @endif
        </div>
        <aside class="dp-detail-side" aria-label="Daten nutzen">
          @template.components.detailActionPanel(
            structureQualityOriginHref = page.structureQualityOriginHref(),
            structureQualityOriginLabel = "Struktur, Qualität und Herkunft",
            exploreHref = databaseMode ? "" : page.exploreHref(),
            usageHref = page.usageHref(),
            openData = page.accessState().openData()
          )
        </aside>
      </div>
    </div>
  </section>
`)
