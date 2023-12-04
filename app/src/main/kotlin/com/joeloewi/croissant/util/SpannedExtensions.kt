package com.joeloewi.croissant.util

import android.graphics.Typeface
import android.text.Layout
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.text.style.AlignmentSpan
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StrikethroughSpan
import android.text.style.StyleSpan
import android.text.style.SubscriptSpan
import android.text.style.SuperscriptSpan
import android.text.style.TypefaceSpan
import android.text.style.URLSpan
import android.text.style.UnderlineSpan
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.UrlAnnotation
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalTextApi::class)
fun Spanned.toAnnotatedString(): AnnotatedString = buildAnnotatedString {
    val spanned = this@toAnnotatedString
    append(spanned.toString())
    getSpans(0, spanned.length, Any::class.java).forEachIndexed { index, span ->
        val start = getSpanStart(span)
        val end = getSpanEnd(span)

        when (span) {
            is AbsoluteSizeSpan -> addStyle(
                SpanStyle(
                    fontSize = if (span.dip) {
                        span.size.dp.value.sp
                    } else {
                        span.size.sp
                    }
                ), start, end
            )

            is AlignmentSpan -> addStyle(
                ParagraphStyle(
                    textAlign = when (span.alignment) {
                        Layout.Alignment.ALIGN_CENTER -> {
                            TextAlign.Center
                        }

                        Layout.Alignment.ALIGN_NORMAL -> {
                            TextAlign.Start
                        }

                        Layout.Alignment.ALIGN_OPPOSITE -> {
                            TextAlign.End
                        }

                        else -> null
                    }
                ), start, end
            )

            is BackgroundColorSpan -> addStyle(
                SpanStyle(background = Color(span.backgroundColor)),
                start,
                end
            )
            /*is BulletSpan -> appendInlineContent()*/
            is ForegroundColorSpan -> addStyle(
                SpanStyle(color = Color(span.foregroundColor)),
                start,
                end
            )
            /*is ImageSpan -> appendInlineContent()*/
            /*is QuoteSpan -> appendInlineContent()*/
            is RelativeSizeSpan -> addStyle(SpanStyle(fontSize = span.sizeChange.em), start, end)
            is StrikethroughSpan -> addStyle(
                SpanStyle(textDecoration = TextDecoration.LineThrough),
                start,
                end
            )

            is StyleSpan -> when (span.style) {
                Typeface.NORMAL -> addStyle(SpanStyle(), start, end)
                Typeface.BOLD -> addStyle(SpanStyle(fontWeight = FontWeight.Bold), start, end)
                Typeface.ITALIC -> addStyle(SpanStyle(fontStyle = FontStyle.Italic), start, end)
                Typeface.BOLD_ITALIC -> addStyle(
                    SpanStyle(
                        fontWeight = FontWeight.Bold,
                        fontStyle = FontStyle.Italic
                    ), start, end
                )
            }

            is SubscriptSpan -> addStyle(
                SpanStyle(baselineShift = BaselineShift.Subscript),
                start,
                end
            )

            is SuperscriptSpan -> addStyle(
                SpanStyle(baselineShift = BaselineShift.Superscript),
                start,
                end
            )

            is TypefaceSpan -> addStyle(
                SpanStyle(
                    fontFamily = when (span.family) {
                        "sans-serif" -> FontFamily.SansSerif
                        "monospace" -> FontFamily.Monospace
                        "serif" -> FontFamily.Serif
                        "cursive" -> FontFamily.Cursive
                        else -> FontFamily.Default
                    }
                ), start, end
            )

            is URLSpan -> addUrlAnnotation(UrlAnnotation(span.url), start, end)
            is UnderlineSpan -> addStyle(
                SpanStyle(textDecoration = TextDecoration.Underline),
                start,
                end
            )
        }
    }
}

/*data class SpanInline(
    val annotatedString: AnnotatedString,
    val keys: List<String>
) {
    companion object {
        @OptIn(ExperimentalTextApi::class)
        fun Spanned.toSpanInline(): SpanInline {
            val keys = mutableListOf<String>()
            val annotatedString = buildAnnotatedString {
                val spanned = this@toSpanInline
                append(spanned.toString())
                getSpans(0, spanned.length, Any::class.java).forEachIndexed { index, span ->
                    val start = getSpanStart(span)
                    val end = getSpanEnd(span)

                    when (span) {
                        is AbsoluteSizeSpan -> addStyle(
                            SpanStyle(
                                fontSize = if (span.dip) {
                                    span.size.dp.value.sp
                                } else {
                                    span.size.sp
                                }
                            ), start, end
                        )
                        is AlignmentSpan -> addStyle(
                            ParagraphStyle(
                                textAlign = when (span.alignment) {
                                    Layout.Alignment.ALIGN_CENTER -> {
                                        TextAlign.Center
                                    }
                                    Layout.Alignment.ALIGN_NORMAL -> {
                                        TextAlign.Start
                                    }
                                    Layout.Alignment.ALIGN_OPPOSITE -> {
                                        TextAlign.End
                                    }
                                    else -> null
                                }
                            ), start, end
                        )
                        is BackgroundColorSpan -> addStyle(
                            SpanStyle(background = Color(span.backgroundColor)),
                            start,
                            end
                        )
                        is BulletSpan -> spanMoshi.adapter(Span::class.java)
                            .toJson(span.withId(id = index.toLong()))
                            .also {
                                keys.add(it)
                            }.let {
                                appendInlineContent(it, it)
                            }
                        is ForegroundColorSpan -> addStyle(
                            SpanStyle(color = Color(span.foregroundColor)),
                            start,
                            end
                        )
                        is ImageSpan -> spanMoshi.adapter(Span::class.java)
                            .toJson(span.withId(id = index.toLong()))
                            .also {
                                keys.add(it)
                            }.let {
                                appendInlineContent(it, it)
                            }
                        is QuoteSpan -> spanMoshi.adapter(Span::class.java)
                            .toJson(span.withId(id = index.toLong()))
                            .also {
                                keys.add(it)
                            }.let {
                                appendInlineContent(it, it)
                            }
                        is RelativeSizeSpan -> addStyle(
                            SpanStyle(fontSize = span.sizeChange.em),
                            start,
                            end
                        )
                        is StrikethroughSpan -> addStyle(
                            SpanStyle(textDecoration = TextDecoration.LineThrough),
                            start,
                            end
                        )
                        is StyleSpan -> when (span.style) {
                            Typeface.NORMAL -> addStyle(SpanStyle(), start, end)
                            Typeface.BOLD -> addStyle(
                                SpanStyle(fontWeight = FontWeight.Bold),
                                start,
                                end
                            )
                            Typeface.ITALIC -> addStyle(
                                SpanStyle(fontStyle = FontStyle.Italic),
                                start,
                                end
                            )
                            Typeface.BOLD_ITALIC -> addStyle(
                                SpanStyle(
                                    fontWeight = FontWeight.Bold,
                                    fontStyle = FontStyle.Italic
                                ), start, end
                            )
                        }
                        is SubscriptSpan -> addStyle(
                            SpanStyle(baselineShift = BaselineShift.Subscript),
                            start,
                            end
                        )
                        is SuperscriptSpan -> addStyle(
                            SpanStyle(baselineShift = BaselineShift.Superscript),
                            start,
                            end
                        )
                        is TypefaceSpan -> addStyle(
                            SpanStyle(
                                fontFamily = when (span.family) {
                                    "sans-serif" -> FontFamily.SansSerif
                                    "monospace" -> FontFamily.Monospace
                                    "serif" -> FontFamily.Serif
                                    "cursive" -> FontFamily.Cursive
                                    else -> FontFamily.Default
                                }
                            ), start, end
                        )
                        is URLSpan -> addUrlAnnotation(UrlAnnotation(span.url), start, end)
                        is UnderlineSpan -> addStyle(
                            SpanStyle(textDecoration = TextDecoration.Underline),
                            start,
                            end
                        )
                    }
                }
            }

            return SpanInline(annotatedString, keys)
        }
    }
}

sealed class Span(
    private val id: Long
) {
    @JsonClass(generateAdapter = true)
    data class Bullet(
        val id: Long,
        val color: Int,
        val bulletRadius: Int,
        val gapWidth: Int
    ) : Span(id) {
        companion object {
            fun BulletSpan.withId(id: Long) = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                Bullet(
                    id = id,
                    color = color,
                    bulletRadius = bulletRadius,
                    gapWidth = gapWidth
                )
            } else {
                Bullet(
                    id = id,
                    color = 0,
                    bulletRadius = 4,
                    gapWidth = 2
                )
            }
        }
    }

    @JsonClass(generateAdapter = true)
    data class Image(
        val id: Long,
        val source: String?
    ) : Span(id) {
        companion object {
            fun ImageSpan.withId(id: Long) = Image(
                id = id,
                source = if (source?.startsWith("//") == true) {
                    "https:$source"
                } else {
                    source
                }
            )
        }
    }

    @JsonClass(generateAdapter = true)
    data class Quote(
        val id: Long,
        val color: Int,
        val stripeWidth: Int,
        val gapWidth: Int
    ) : Span(id) {
        companion object {
            fun QuoteSpan.withId(id: Long) = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                Quote(
                    id = id,
                    color = color,
                    stripeWidth = stripeWidth,
                    gapWidth = gapWidth
                )
            } else {
                Quote(
                    id = id,
                    color = color,
                    stripeWidth = 2,
                    gapWidth = 2
                )
            }
        }
    }
}

private val spanMoshi = Moshi.Builder()
    .add(
        PolymorphicJsonAdapterFactory.of(
            Span::class.java,
            "type"
        ).withSubtype(
            Span.Bullet::class.java,
            "bullet"
        ).withSubtype(
            Span.Image::class.java,
            "image"
        ).withSubtype(
            Span.Quote::class.java,
            "quote"
        )
    )
    .build()

@Composable
fun SpanInlineText(
    modifier: Modifier = Modifier,
    spanInline: SpanInline,
    image: @Composable (source: String?) -> Unit,
    bullet: @Composable (color: Int, bulletRadius: Int, gapWidth: Int) -> Unit,
    quote: @Composable (color: Int, stripeWidth: Int, gapWidth: Int) -> Unit,
) {
    val updatedImage by rememberUpdatedState(newValue = image)
    val updatedBullet by rememberUpdatedState(newValue = bullet)
    val updatedQuote by rememberUpdatedState(newValue = quote)

    val inlineContentMap = spanInline.keys.associateWith { key ->
        InlineTextContent(
            placeholder = Placeholder(
                width = 1.7.em,
                height = 23.sp,
                placeholderVerticalAlign = PlaceholderVerticalAlign.AboveBaseline
            ),
            children = {
                key(key) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        with(spanMoshi.adapter(Span::class.java).fromJson(key)!!) {
                            when (this) {
                                is Span.Bullet -> {
                                    updatedBullet(
                                        color = color,
                                        bulletRadius = bulletRadius,
                                        gapWidth = gapWidth
                                    )
                                }
                                is Span.Image -> {
                                    updatedImage(source = source)
                                }
                                is Span.Quote -> {
                                    updatedQuote(
                                        color = color,
                                        stripeWidth = stripeWidth,
                                        gapWidth = gapWidth
                                    )
                                }
                            }
                        }
                    }
                }
            }
        )
    }

    Text(
        modifier = modifier,
        text = spanInline.annotatedString,
        inlineContent = inlineContentMap
    )
}*/
