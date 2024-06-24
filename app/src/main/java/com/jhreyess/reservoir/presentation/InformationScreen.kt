package com.jhreyess.reservoir.presentation

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jhreyess.reservoir.BuildConfig
import com.jhreyess.reservoir.ui.theme.ReservoirTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InformationScreen(
    onBackPressed: () -> Unit = {}
) {
    val context = LocalContext.current
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = "Información") },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        }
    ) { contentPadding ->
        Surface(
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier.padding(contentPadding)
        ) {
           Column(
               modifier = Modifier.verticalScroll(rememberScrollState())
           ) {
               Section(
                   title = "Propósito y uso",
                   description = "Esta aplicación muestra los niveles de agua de las presas en el " +
                       "estado de Nuevo León, proporcionando datos actualizados para la difusión " +
                       "del estado hídrico. La aplicación no tiene fines de lucro " +
                       "y busca únicamente proporcionar información útil y precisa para el " +
                       "beneficio de la comunidad."
               )
               Section(
                   title = "Fuentes de datos",
                   description = "Los datos son obtenidos por medio del Sistema Nacional de " +
                       "Información del Agua (SINA) de la CONAGUA, con base en información del " +
                       "Sistema de Información Hidrica (SIH) y con actualizaciones realizadas diariamente. " +
                       "Esta aplicación no pertenece al gobierno ni tiene ningún tipo de " +
                       "afiliación o contacto con entidades gubernamentales. Los datos proporcionados " +
                       "son con fines informativos y se obtienen de fuentes públicas disponibles."
               ) {
                   Text(
                       text = "CONAGUA. Gerencia de Planificación Hídrica. " +
                           "Sistema Nacional de Información del Agua (SINA) " +
                           "https://sinav30.conagua.gob.mx:8080/",
                       fontSize = 14.sp,
                       lineHeight = 18.sp,
                       modifier = Modifier.padding(16.dp)
                   )
               }
               Section(
                   title = "Comprendiendo los niveles de agua",
                   description = "Los Niveles de Agua Maximos Ordinarios (NAMO) son el nivel máximo " +
                       "de agua con el que se puede operar una presa para satisfacer diversas " +
                       "demandas, como agua potable, generación de energía y riego. Este nivel varía " +
                       "dependiendo de si el vertedor de la presa (estructura que permite la salida " +
                       "controlada del agua) está controlado por compuertas o no, así como de la " +
                       "temporada (estiaje/lluvias)."
               )
               Section(
                   title = "Valora la aplicación",
                   description = "Si te ha gustado la app y te parece útil, te invitamos a dejar " +
                       "una valoración en la tienda de aplicaciones.",
               ) {
                   InfoButton(
                       text = "Ir a playstore",
                       icon = Icons.Rounded.Star,
                       onClick = {
                           val intent = Intent(Intent.ACTION_VIEW).apply {
                               data = Uri.parse(
                                   "https://play.google.com/store/apps/details?id=com.example.android")
                               setPackage("com.android.vending")
                           }
                           context.startActivity(intent)
                       }
                   )
               }
               Spacer(modifier = Modifier.weight(1f))
               Text(
                   text = "Versión de la app v${BuildConfig.VERSION_NAME}",
                   textAlign = TextAlign.Center,
                   modifier = Modifier
                       .fillMaxWidth()
                       .padding(vertical = 8.dp)
               )
           }
        }
    }
}

@Composable
private fun Section(
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit = {}
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Column {
            Column(Modifier.padding(16.dp)) {
                Text(
                    text = title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = description,
                    fontSize = 14.sp,
                    lineHeight = 16.sp,
                    )
            }
            content()
        }
    }
    Spacer(
        modifier = Modifier
            .height(8.dp)
            .fillMaxWidth()
    )
}


@Composable
private fun InfoButton(
    text: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    OutlinedButton(
        modifier = modifier
            .fillMaxWidth(),
        onClick = onClick,
        shape = RoundedCornerShape(4.dp),
        contentPadding = PaddingValues(16.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        ),
        border = BorderStroke(1.dp, Color.Transparent)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = text)
            }
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null
            )
        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewInformationScreen() {
    ReservoirTheme {
        InformationScreen()
    }
}