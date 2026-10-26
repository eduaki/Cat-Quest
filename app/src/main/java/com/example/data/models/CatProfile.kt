package com.example.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cat_profile")
data class CatProfile(
    @PrimaryKey
    val id: Int = 1,
    val name: String = "Mingau",
    val gender: String = "Macho", // Macho, Fêmea, Outro
    val breed: String = "Siamês Peludinho",
    val favoriteSnack: String = "Sachê de Salmão",
    val birthday: String = "2024-05-15",
    val avatarEmoji: String = "pets",
    val totalScore: Int = 0, // Accumulated score / Care points
    val level: Int = 1, // Caregiver level
    val fishCoins: Int = 100, // Starter bonus!
    val pawsCount: Int = 0, // Total missions completed with photo
    val equippedAccessory: String = "NONE", // NONE, BOW, CROWN, TOPHAT, CAP, WITCH, FLOWERS, SUNGLASSES, BRAZIL_BANDANA
    val equippedSkin: String = "WHITE", // WHITE, GINGER, BLACK, SIAMESE, CALICO, TUXEDO, TABBY, SPOTTED_GRAY, SPOTTED_BROWN, etc.
    val equippedTheme: String = "DEFAULT", // DEFAULT, SAKURA, MINT_JARDIM, NIGHT_STARS
    val unlockedItems: String = "NONE,WHITE,DEFAULT" // Comma separated list of unlocked IDs
) {
    fun hasUnlocked(itemId: String): Boolean {
        return unlockedItems.split(",").contains(itemId)
    }

    fun withUnlocked(itemId: String): String {
        val current = unlockedItems.split(",").toMutableSet()
        current.add(itemId)
        return current.joinToString(",")
    }
}

enum class RewardType(val displayName: String) {
    ACCESSORY("Acessório"),
    SKIN("Pelagem"),
    THEME("Tema Decorativo")
}

data class ShopRewardItem(
    val id: String,
    val name: String,
    val type: RewardType,
    val price: Int,
    val iconName: String,
    val description: String
)

object RewardCatalog {
    val accessories = listOf(
        ShopRewardItem("NONE", "Nenhum (Natural)", RewardType.ACCESSORY, 0, "pets", "O charme natural do seu gatinho"),
        ShopRewardItem("BOW", "Lacinho Rosa", RewardType.ACCESSORY, 50, "celebration", "Um lacinho delicado e muito fofo"),
        ShopRewardItem("CROWN", "Coroa Real Felina", RewardType.ACCESSORY, 150, "emoji_events", "Para o verdadeiro rei ou rainha da casa"),
        ShopRewardItem("TOPHAT", "Cartola de Cavalheiro", RewardType.ACCESSORY, 180, "theater_comedy", "Miau elegante e refinado"),
        ShopRewardItem("CAP", "Boné Descolado", RewardType.ACCESSORY, 100, "checkroom", "Gatinho radical e urbano"),
        ShopRewardItem("WITCH", "Chapéu Mágico", RewardType.ACCESSORY, 220, "auto_awesome", "Gatinho bruxinho cheio de magia"),
        ShopRewardItem("FLOWERS", "Coroa de Flores", RewardType.ACCESSORY, 120, "local_florist", "Primavera e cheirinho de flor"),
        ShopRewardItem("SUNGLASSES", "Óculos Escuros Miau", RewardType.ACCESSORY, 140, "wb_sunny", "Miau estiloso demais para o sol"),
        ShopRewardItem("BRAZIL_BANDANA", "Bandana Brasil", RewardType.ACCESSORY, 160, "flag", "Uma bandana verde e amarela para torcer pelo Brasil")
    )

    val skins = listOf(
        ShopRewardItem("WHITE", "Branco Algodão", RewardType.SKIN, 0, "brush", "Pelagem macia como neve"),
        ShopRewardItem("GINGER", "Laranja Tigrado", RewardType.SKIN, 0, "brush", "Alegre, sapeca e cheio de energia"),
        ShopRewardItem("BLACK", "Preto Pantera", RewardType.SKIN, 0, "brush", "Pelos negros brilhantes e elegantes"),
        ShopRewardItem("SIAMESE", "Siamês Charmoso", RewardType.SKIN, 0, "brush", "Máscara e orelhas escuras clássicas"),
        ShopRewardItem("CALICO", "Calico Tricolor", RewardType.SKIN, 0, "brush", "Tricolor que traz muita fortuna"),
        ShopRewardItem("TUXEDO", "Frajola Elegante", RewardType.SKIN, 0, "brush", "Preto com peito e patinhas brancas"),
        ShopRewardItem("TABBY", "Cinza Tigrado", RewardType.SKIN, 0, "brush", "Clássico riscadinho cinza"),
        ShopRewardItem("SPOTTED_GRAY", "Manchas de Cinza", RewardType.SKIN, 0, "brush", "Branco com manchas cinzas"),
        ShopRewardItem("SPOTTED_BROWN", "Manchas Chocolate", RewardType.SKIN, 0, "brush", "Branco com manchas marrons"),
        ShopRewardItem("SPOTTED_BLACK", "Vaca Malhada", RewardType.SKIN, 0, "brush", "Branco com grandes manchas pretas"),
        ShopRewardItem("SPOTTED_ORANGE", "Manchas de Fogo", RewardType.SKIN, 0, "brush", "Branco com manchas alaranjadas")
    )

    val themes = listOf(
        ShopRewardItem("DEFAULT", "Pêssego Aconchegante", RewardType.THEME, 0, "palette", "O visual clássico e acolhedor"),
        ShopRewardItem("SAKURA", "Jardim de Sakura", RewardType.THEME, 100, "local_florist", "Tons de cerejeira e pétalas suaves"),
        ShopRewardItem("MINT_JARDIM", "Catnip Verde Menta", RewardType.THEME, 130, "eco", "Refrescante como um jardim de catnip"),
        ShopRewardItem("NIGHT_STARS", "Céu Estrelado Felino", RewardType.THEME, 170, "nights_stay", "Noite mágica para sonhar com peixinhos")
    )
}
