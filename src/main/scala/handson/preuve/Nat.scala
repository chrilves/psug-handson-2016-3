package handson.preuve

sealed abstract class Nat           // Entiers naturel au type-level
final class Z           extends Nat // Le type de 0
final class S[n <: Nat] extends Nat // Le type de n + 1