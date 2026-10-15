
package eu.kanade.tachiyomi.animeextension.all.javlibrary


import eu.kanade.tachiyomi.animesource.model.AnimeFilterList
import eu.kanade.tachiyomi.animesource.model.AnimesPage
import eu.kanade.tachiyomi.animesource.model.SAnime
import eu.kanade.tachiyomi.animesource.online.AnimeHttpSource
import eu.kanade.tachiyomi.network.GET
import eu.kanade.tachiyomi.util.asJsoup
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import okhttp3.Request
import okhttp3.Response
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element


class JavLibrary : AnimeHttpSource() {
    override val name = "Jav Library"
    override val baseUrl = "https://avernus139.github.io/JavLibrary"
    override val lang = "en"
    override val supportsLatest = false

    override fun popularAnimeRequest(page: Int): Request =
        GET(baseUrl, headers)

    override fun popularAnimeParse(response: Response): AnimesPage {
        val document = response.asJsoup()
        return AnimesPage(parseCards(document), false)
    }

    override fun searchAnimeRequest(
        page: Int,
        query: String,
        filters: AnimeFilterList,
    ): Request = GET(baseUrl, headers)

    override fun searchAnimeParse(response: Response): AnimesPage {
        val document = response.asJsoup()
        val query = response.request.url.queryParameter("q").orEmpty().trim()

        val results = parseCards(document).filter {
            query.isBlank() || it.title.contains(query, ignoreCase = true)
        }

        return AnimesPage(results, false)
    }

    override fun animeDetailsParse(response: Response): SAnime {
        val document = response.asJsoup()
        val title = response.request.url.queryParameter("title").orEmpty()

        return parseCards(document).firstOrNull {
            it.title.equals(title, ignoreCase = true)
        } ?: SAnime.create().apply {
            this.title = title.ifBlank { "Jav Library item" }
            this.url = "/"
        }
    }

    override fun latestUpdatesParse(response: Response): AnimesPage =
        AnimesPage(emptyList(), false)

    override fun getFilterList(): AnimeFilterList = AnimeFilterList()

    private fun parseCards(document: Document): List<SAnime> =
        document.select(".movie-grid .movie")
            .mapNotNull(::parseCard)
            .distinctBy { it.title.lowercase() }

    private fun parseCard(element: Element): SAnime? {
        val title = element.selectFirst("p")?.text()?.trim().orEmpty()
        val image = element.selectFirst("img")
        val cover = image?.absUrl("src").orEmpty().ifBlank {
            image?.attr("src").orEmpty()
        }

        if (title.isBlank()) return null

        val encodedTitle = URLEncoder.encode(
            title,
            StandardCharsets.UTF_8.name(),
        )

        return SAnime.create().apply {
            this.title = title
            this.url = "/?title=$encodedTitle"
            this.thumbnail_url = cover
        }
    }
}
