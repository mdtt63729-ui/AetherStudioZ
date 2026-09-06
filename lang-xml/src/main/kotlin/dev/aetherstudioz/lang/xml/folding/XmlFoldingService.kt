package dev.aetherstudioz.lang.xml.folding

import dev.aetherstudioz.lang.dom.ParsedFile
import dev.aetherstudioz.lang.folding.FoldRegion
import dev.aetherstudioz.lang.folding.FoldingService
import dev.aetherstudioz.lang.xml.PsiXmlProjection
import dev.aetherstudioz.vfs.VirtualFile

/**
 * Code folding for XML: collapses each element's body (`<tag>…</tag>` → `<tag>...</tag>`) and each block
 * comment. Ranges come straight from the PSI (exact `XmlTagValue` / `XmlComment` spans via
 * [PsiXmlProjection.folds]); the editor drops single-line / zero-width regions, so nothing needs pre-filtering.
 *
 * It re-parses the buffer text to PSI (cheap for XML) rather than reconstructing body ranges from the neutral
 * DOM, since PSI exposes the between-tags content range precisely. [parseOf] yields the cached parse so the
 * text matches what the editor displays.
 */
class XmlFoldingService(private val parseOf: suspend (VirtualFile) -> ParsedFile?) :
    FoldingService {

    override suspend fun folds(file: VirtualFile): List<FoldRegion> {
        val text = parseOf(file)?.text() ?: return emptyList()
        return PsiXmlProjection.folds(file.path, text)
    }
}
