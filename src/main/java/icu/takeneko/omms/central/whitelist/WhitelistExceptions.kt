package icu.takeneko.omms.central.whitelist

class WhitelistAlreadyExistsException(val whitelistName: String) :
    RuntimeException("Whitelist $whitelistName already exists.")

class WhitelistNotExistException(val whitelistName: String) : Exception("Whitelist $whitelistName not exist.")
