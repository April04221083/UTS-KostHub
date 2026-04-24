// ============================================================
// KOST-HUB - Sistem Manajemen Kos Mahasiswa
// UTS Pemrograman Berorientasi Objek
// ============================================================

class Kamar(
    val nomorKamar: String,
    val tipeKamar: String,
    val hargaBulanan: Double
) {
    private var statusKamar: String = "Tersedia"
    private var idPenyewaAktif: String? = null

    fun getStatus(): String = statusKamar
    fun getIdPenyewa(): String? = idPenyewaAktif
    fun getHarga(): Double = hargaBulanan

    fun isAvailable(): Boolean {
        return statusKamar == "Tersedia"
    }

    fun pesanKamar(idPenyewa: String): Boolean {
        if (statusKamar == "Terisi") {
            println("[GAGAL] Kamar $nomorKamar sudah terisi. Pemesanan ditolak.")
            return false
        }

        statusKamar = "Terisi"
        idPenyewaAktif = idPenyewa
        println("[SUKSES] Kamar $nomorKamar berhasil dipesan oleh penyewa ID: $idPenyewa.")
        return true
    }

    fun cetakInfo() {
        println("Kamar $nomorKamar | Tipe: $tipeKamar | Harga: Rp${hargaBulanan.toLong()} | Status: $statusKamar")
    }
}

class Penyewa(
    val idPenyewa: String,
    val nama: String,
    saldoAwal: Double,
    private val pin: String
) {
    var saldo: Double = saldoAwal
        private set

    var statusPembayaran: String = "Belum Lunas"
        private set

    fun topUpSaldo(jumlah: Double): Boolean {
        if (jumlah <= 0) {
            println("[GAGAL] Top up harus lebih dari 0.")
            return false
        }

        saldo += jumlah
        println("[SUKSES] Top up Rp${jumlah.toLong()} berhasil. Saldo $nama sekarang Rp${saldo.toLong()}.")
        return true
    }

    fun bayarTagihan(kamar: Kamar, inputPin: String): Boolean {
        val harga = kamar.getHarga()

        if (inputPin != pin) {
            println("[GAGAL] PIN salah. Pembayaran ditolak.")
            return false
        }

        if (kamar.getIdPenyewa() != idPenyewa) {
            println("[GAGAL] Kamar ${kamar.nomorKamar} bukan milik $nama.")
            return false
        }

        if (saldo < harga) {
            println("[GAGAL] Saldo $nama tidak cukup. Saldo: Rp${saldo.toLong()}, Tagihan: Rp${harga.toLong()}.")
            return false
        }

        saldo -= harga
        statusPembayaran = "Lunas"
        println("[SUKSES] $nama berhasil membayar tagihan kamar ${kamar.nomorKamar}.")
        println("Sisa saldo: Rp${saldo.toLong()} | Status pembayaran: $statusPembayaran")
        return true
    }

    fun cetakInfo() {
        println("ID: $idPenyewa | Nama: $nama | Saldo: Rp${saldo.toLong()} | Status: $statusPembayaran")
    }
}

class BapakKos(
    val nama: String
) {
    private var pendapatan: Double = 0.0
    private val daftarKamar: MutableList<Kamar> = mutableListOf()

    fun tambahKamar(kamar: Kamar) {
        daftarKamar.add(kamar)
        println("[INFO] Kamar ${kamar.nomorKamar} berhasil ditambahkan.")
    }

    fun terimaPembayaran(penyewa: Penyewa, kamar: Kamar, pin: String): Boolean {
        println("\n[PROSES] Pembayaran dari ${penyewa.nama} sedang diproses...")

        val sukses = penyewa.bayarTagihan(kamar, pin)

        if (sukses) {
            pendapatan += kamar.getHarga()
            println("[INFO] Pendapatan $nama bertambah. Total pendapatan: Rp${pendapatan.toLong()}.")
        }

        return sukses
    }

    fun lihatDaftarKamar() {
        println("\n=== DAFTAR KAMAR ===")
        for (kamar in daftarKamar) {
            kamar.cetakInfo()
        }
    }

    fun lihatLaporanKeuangan() {
        println("\n=== LAPORAN KEUANGAN ===")
        println("Nama Bapak Kos: $nama")
        println("Total Pendapatan: Rp${pendapatan.toLong()}")
    }
}

fun main() {
    println("========================================")
    println(" SIMULASI SISTEM KOST-HUB")
    println("========================================")

    val bapakKos = BapakKos("Pak Bambang")

    val kamarA = Kamar("A-01", "Standard", 1500000.0)
    val kamarB = Kamar("B-01", "Deluxe", 2000000.0)

    val penyewa1 = Penyewa("PY-001", "Putri", 3000000.0, "1234") 
    val penyewa2 = Penyewa("PY-002", "Jara", 1200000.0, "5678") 

    println("\n=== SETUP AWAL ===")
    bapakKos.tambahKamar(kamarA)
    bapakKos.tambahKamar(kamarB)
    bapakKos.lihatDaftarKamar()

    println("\n=== SIMULASI 1: PEMESANAN KAMAR ===")

    println("\n[TEST SUKSES] Putri memesan kamar A-01")
    kamarA.pesanKamar(penyewa1.idPenyewa)

    println("\n[TEST GAGAL] Jara mencoba memesan kamar A-01 yang sudah terisi")
    kamarA.pesanKamar(penyewa2.idPenyewa)

    println("\n[TEST SUKSES] Jara memesan kamar B-01")
    kamarB.pesanKamar(penyewa2.idPenyewa)

    bapakKos.lihatDaftarKamar()

    println("\n=== SIMULASI 2: PEMBAYARAN TAGIHAN ===")

    println("\n[INFO] Data penyewa sebelum pembayaran:")
    penyewa1.cetakInfo()
    penyewa2.cetakInfo()

    println("\n[TEST GAGAL] Jara membayar dengan PIN salah")
    bapakKos.terimaPembayaran(penyewa2, kamarB, "0000")

    println("\n[TEST GAGAL] Jara membayar kamar milik Putri")
    bapakKos.terimaPembayaran(penyewa2, kamarA, "5678")

    println("\n[TEST GAGAL] Jara membayar dengan saldo tidak cukup")
    bapakKos.terimaPembayaran(penyewa2, kamarB, "5678")

    println("\n[TEST SUKSES] Putri membayar tagihan kamar A-01")
    bapakKos.terimaPembayaran(penyewa1, kamarA, "1234")

    println("\n[TEST SUKSES] Jara top up lalu membayar tagihan")
    penyewa2.topUpSaldo(1000000.0)
    bapakKos.terimaPembayaran(penyewa2, kamarB, "5678")

    println("\n=== SIMULASI 3: VALIDASI TOP UP ===")

    println("\n[TEST GAGAL] Top up dengan nilai negatif")
    penyewa1.topUpSaldo(-500000.0)

    println("\n[TEST GAGAL] Top up dengan nilai nol")
    penyewa1.topUpSaldo(0.0)

    println("\n=== SIMULASI 4: DATA HIDING ===")
    println("Saldo tidak bisa diubah langsung dari luar class karena menggunakan private set.")
    println("PIN tidak bisa diakses dari luar class karena bersifat private.")
    println("Status kamar tidak bisa diubah sembarangan karena bersifat private.")

    println("\n=== STATUS AKHIR PENYEWA ===")
    penyewa1.cetakInfo()
    penyewa2.cetakInfo()

    bapakKos.lihatLaporanKeuangan()
    bapakKos.lihatDaftarKamar()

    println("\n========================================")
    println(" SIMULASI SELESAI")
    println("========================================")
}
