package com.nichoshop.servlets

import com.nichoshop.model.AccountType
import org.json4s.CustomSerializer
import org.json4s.JsonAST.JString

object AccountTypeSerializer extends CustomSerializer[AccountType](format => ( {
  case JString("PERSONAL") => AccountType.PERSONAL
  case JString("BUSINESS") => AccountType.BUSINESS
  case JString("SYSTEM") => AccountType.SYSTEM
}, {
  case AccountType.PERSONAL => JString("PERSONAL")
  case AccountType.BUSINESS => JString("BUSINESS")
  case AccountType.SYSTEM => JString("SYSTEM")
}
))
