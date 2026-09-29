package com.example.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cat_profile")
data class CatProfile(
    @PrimaryKey
    val id: Int = 1,
    val name: String = "Mingau",
    val breed: String = "Siamês Peludinho",
    val favoriteSnack: String = "Sachê de Salmão",
    val birthday: String = "2024-05-15",
    val avatarEmoji: String = "🐱",
    val totalScore: Int = 0, // Accumulated score / Care points
    val level: Int = 1, // Caregiver level
    val fishCoins: Int = 100, // Starter bonus!
    val pawsCount: Int = 0, // Total missions completed with photo
    val equippedAccessory: String = "NONE", // NONE, BOW, CROWN, TOPHAT, CAP, WITCH, FLOWERS, SUNGLASSES
    val equippedSkin: String = "WHITE", // WHITE, GINGER, BLACK, SIAMESE, CALICO
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
    val iconEmoji: String,
    val description: String
)

object RewardCatalog {
    val accessories = listOf(
        ShopRewardItem("NONE", "Nenhum (Natural)", RewardType.ACCESSORY, 0, "🐱", "O charme natural do seu gatinho"),
        ShopRewardItem("BOW", "Lacinho Rosa Kawaii", RewardType.ACCESSORY, 50, "🎀", "Um lacinho delicado e muito fofo"),
        ShopRewardItem("CROWN", "Coroa Real Felina", RewardType.ACCESSORY, 150, "👑", "Para o verdadeiro rei/rainha da casa"),
        ShopRewardItem("TOPHAT", "Cartola de Cavalheiro", RewardType.ACCESSORY, 180, "🎩", "Miau elegante e refinado"),
        ShopRewardItem("CAP", "Boné Descolado", RewardType.ACCESSORY, 100, "🧢", "Gatinho radical e urbano"),
        ShopRewardItem("WITCH", "Chapéu Mágico", RewardType.ACCESSORY, 220, "🧙", "Gatinho bruxinho cheio de magia"),
        ShopRewardItem("FLOWERS", "Coroa de Flores", RewardType.ACCESSORY, 120, "🌸", "Primavera e cheirinho de flor"),
        ShopRewardItem("SUNGLASSES", "Óculos Escuros Miau", RewardType.ACCESSORY, 140, "🕶️", "Miau estiloso demais pra olhar o sol")
    )

    val skins = listOf(
        ShopRewardItem("WHITE", "Algodão Doce (Branco)", RewardType.SKIN, 0, "⚪", "Pelagem macia como neve"),
        ShopRewardItem("GINGER", "Laranjinha Tigrado", RewardType.SKIN, 80, "🟠", "Alegre, sapeca e cheio de energia"),
        ShopRewardItem("BLACK", "Panterinha da Sorte", RewardType.SKIN, 120, "⚫", "Pelos negros brilhantes e elegantes"),
        ShopRewardItem("SIAMESE", "Siamês Charmoso", RewardType.SKIN, 160, "🟤", "Máscara e orelhas escuras clássicas"),
        ShopRewardItem("CALICO", "Calico da Sorte", RewardType.SKIN, 200, "🐱", "Tricolor que traz muita fortuna")
    )

    val themes = listOf(
        ShopRewardItem("DEFAULT", "Pêssego Aconchegante", RewardType.THEME, 0, "🍑", "O visual clássico e acolhedor"),
        ShopRewardItem("SAKURA", "Jardim de Sakura", RewardType.THEME, 100, "🌸", "Tons de cerejeira e pétalas suaves"),
        ShopRewardItem("MINT_JARDIM", "Catnip Verde Menta", RewardType.THEME, 130, "🌿", "Refrescante como um jardim de catnip"),
        ShopRewardItem("NIGHT_STARS", "Céu Estrelado Felino", RewardType.THEME, 170, "🌌", "Noite mágica para sonhar com peixinhos")
    )
}
