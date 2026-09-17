package com.catcorp.catquest.data

import kotlin.random.Random

object ChallengeGenerator {
    private val challenges = listOf(
        "Tire uma foto do seu pet bocejando!",
        "Capture seu pet esticado tirando uma soneca.",
        "Fotografe o focinho ou as patinhas do seu pet em close-up.",
        "Tire uma foto do seu pet olhando pela janela.",
        "Capture o momento em que seu pet está brincando com o brinquedo favorito.",
        "Tire uma foto do seu pet junto com você (uma selfie!).",
        "Fotografe seu pet esperando a comida ou petisco.",
        "Capture um momento engraçado ou uma pose curiosa do seu pet.",
        "Tire uma foto do seu pet explorando um cantinho novo da casa.",
        "Fotografe o reflexo dos olhos do seu pet ou o olhar dele fixo em você."
    )

    fun getChallengeForDate(dateString: String): String {
        // Usa o hash da data como semente para garantir o mesmo desafio no mesmo dia
        val seed = dateString.hashCode().toLong()
        val random = Random(seed)
        val index = random.nextInt(challenges.size)
        return challenges[index]
    }
}
