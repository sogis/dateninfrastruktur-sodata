@import ch.so.agi.datenportal.web.view.UsagePageVm
@param UsagePageVm page
@param String requestAccessUrl = null
@param boolean databaseMode = false
@template.layouts.main(chrome = page.chrome(), databaseMode = databaseMode, content = @`
  <section class="dp-detail-page dp-usage-page">
    <div class="dp-container">
      @if(requestAccessUrl != null)
        <p><a class="dp-action-pill" href="${requestAccessUrl}">Datenzugang anfragen</a> · <a href="/access/me">Meine Zugänge</a></p>
      @endif
      <header class="dp-detail-hero dp-usage-hero">
        <h1 class="dp-detail-title">${page.title()}</h1>
      </header>
      <div class="dp-detail-summary-layout dp-usage-layout">
        <div class="dp-detail-summary-main">
          @template.components.usageDirectAccess(rows = page.directAccessRows())
          @if(!page.codeExamples().isEmpty())
            @template.components.usageCodeExamples(examples = page.codeExamples())
          @endif
        </div>
      </div>
    </div>
  </section>
`)
