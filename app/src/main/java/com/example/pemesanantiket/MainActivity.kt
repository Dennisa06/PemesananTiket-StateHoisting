package com.example.pemesanantiket

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import com.example.pemesanantiket.ui.theme.PemesananTiketTheme
import kotlinx.coroutines.delay


// Warna utama
private val BiruUtama = Color(0xFF3F51B5)
private val BiruMuda = Color(0xFFDCE4F7) // biru soft untuk tombol - dan +

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            PemesananTiketTheme {

                // STATE (dikelola PARENT/onCreate)
                // 1. Harga tiket per lembar
                var hargaTiket by rememberSaveable { mutableIntStateOf(50000) }

                // 2. Jumlah tiket yang dipesan
                var jumlahTiket by rememberSaveable { mutableIntStateOf(1) }

                // 3. Nama pembeli tiket
                var namaPembeli by rememberSaveable { mutableStateOf("") }

                // Status pesanan yang ditampilkan ke pengguna
                var statusPesan by rememberSaveable { mutableStateOf("Silakan pesan tiket") }

                // Penanda proses pemesanan sedang berjalan (untuk menonaktifkan tombol)
                var sedangMemproses by rememberSaveable { mutableStateOf(false) }

                // Pemicu (trigger) yang berubah setiap kali tombol "Pesan Tiket" ditekan.
                // Nilai ini dipakai sebagai KEY pada LaunchedEffect.
                var pemesananTrigger by rememberSaveable { mutableIntStateOf(0) }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(BiruUtama)
                                .statusBarsPadding()
                                .padding(horizontal = 20.dp, vertical = 24.dp)
                        ) {
                            Text(
                                text = "Pemesanan Tiket",
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 22.sp
                            )
                        }
                    },
                    containerColor = Color(0xFFF7F7FA)
                ) { innerPadding ->

                    // SIDE-EFFECT
                    // Berjalan setiap kali pemesananTrigger berubah (setiap klik tombol).
                    LaunchedEffect(pemesananTrigger) {
                        if (pemesananTrigger > 0) {
                            if (namaPembeli.isBlank()) {
                                // Validasi: nama pembeli belum diisi
                                statusPesan = "Nama harus diisi"
                                sedangMemproses = false
                            } else {
                                // Simulasi proses pemesanan tiket
                                sedangMemproses = true
                                statusPesan = "Memproses pesanan..."
                                delay(5000) // delay 5 detik
                                statusPesan = "Tiket berhasil dipesan!"
                                sedangMemproses = false
                            }
                        }
                    }

                    // UI (child, stateless)
                    // State turun lewat parameter, event naik lewat callback.
                    TicketBookingScreen(
                        modifier = Modifier.padding(innerPadding),
                        namaPembeli = namaPembeli,
                        onNamaChange = { namaPembeli = it },
                        jumlahTiket = jumlahTiket,
                        onJumlahBertambah = { jumlahTiket++ },
                        onJumlahBerkurang = { if (jumlahTiket > 1) jumlahTiket-- },
                        hargaTiket = hargaTiket,
                        statusPesan = statusPesan,
                        tombolAktif = !sedangMemproses,
                        onPesanClick = { pemesananTrigger++ }
                    )
                }
            }
        }
    }
}

/**
 * Composable STATELESS (hasil State Hoisting).
 * Tidak menyimpan state apa pun sendiri
 */
@Composable
fun TicketBookingScreen(
    modifier: Modifier = Modifier,
    namaPembeli: String,
    onNamaChange: (String) -> Unit,
    jumlahTiket: Int,
    onJumlahBertambah: () -> Unit,
    onJumlahBerkurang: () -> Unit,
    hargaTiket: Int,
    statusPesan: String,
    tombolAktif: Boolean,
    onPesanClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()) // tetap bisa di-scroll saat landscape
            .padding(20.dp)
    ) {
        // Label & input Nama
        Text(text = "Nama", fontWeight = FontWeight.Medium, color = Color(0xFF444444))
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = namaPembeli,
            onValueChange = onNamaChange,
            placeholder = { Text("Masukkan nama Anda", color = Color(0xFFBBBBBB)) },
            singleLine = true,
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = BiruUtama,
                unfocusedBorderColor = Color(0xFFDDDDDD)
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Label & kontrol Jumlah Tiket
        Text(text = "Jumlah Tiket", fontWeight = FontWeight.Medium, color = Color(0xFF444444))
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            KontrolBulatKotak(label = "-", onClick = onJumlahBerkurang)
            Text(
                text = "$jumlahTiket",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )
            KontrolBulatKotak(label = "+", onClick = onJumlahBertambah)
        }

        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Total: Rp${"%,d".format(hargaTiket * jumlahTiket).replace(',', '.')}",
            color = Color(0xFF888888),
            fontSize = 13.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Tombol Pesan Tiket
        Button(
            onClick = onPesanClick,
            enabled = tombolAktif,
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = BiruUtama,
                disabledContainerColor = Color(0xFFB9BEE0)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text(text = "Pesan Tiket", color = Color.White, fontWeight = FontWeight.Medium)
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Kotak Status
        StatusBox(statusPesan = statusPesan)

        Spacer(modifier = Modifier.height(20.dp))
    }
}

/** Tombol kotak untuk kontrol jumlah tiket (-/+) */
@Composable
private fun KontrolBulatKotak(label: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = BiruMuda,
            contentColor = BiruUtama
        ),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp),
        modifier = Modifier
            .width(64.dp)
            .height(44.dp)
    ) {
        Text(text = label, fontSize = 18.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun StatusBox(statusPesan: String) {
    val isNamaKosong = statusPesan.contains("harus diisi", ignoreCase = true)
    val isMemproses = statusPesan.contains("Memproses", ignoreCase = true)
    val isBerhasil = statusPesan.contains("berhasil", ignoreCase = true)

    val hijau = Color(0xFF2E7D32)
    val biru = BiruUtama
    val merah = Color(0xFFD32F2F)
    val merahTua = Color(0xFF8B0000)

    val warnaLatar = when {
        isNamaKosong -> Color(0xFFFDECEC)
        isBerhasil -> Color(0xFFE8F5E9)
        isMemproses -> Color(0xFFE8EAF6)
        else -> Color(0xFFF1F1F4)
    }
    val warnaLabel = if (isNamaKosong) merahTua else Color.Black
    val warnaPesan = when {
        isNamaKosong -> merah
        isBerhasil -> hijau
        isMemproses -> biru
        else -> Color.Black
    }

    Surface(
        color = warnaLatar,
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            when {
                isMemproses -> CircularProgressIndicator(
                    color = biru,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(16.dp)
                )
                isBerhasil -> Text(text = "✓", color = hijau, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                isNamaKosong -> Text(text = "!", color = merah, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            val teksStatus = buildAnnotatedString {
                withStyle(SpanStyle(color = warnaLabel, fontWeight = FontWeight.Bold)) {
                    append("Status")
                }
                withStyle(SpanStyle(color = warnaPesan, fontWeight = FontWeight.Normal)) {
                    append(": $statusPesan")
                }
            }
            Text(text = teksStatus, fontSize = 14.sp)
        }
    }
}