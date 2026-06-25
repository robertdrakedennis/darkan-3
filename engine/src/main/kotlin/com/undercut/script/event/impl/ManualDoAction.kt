package com.undercut.script.event.impl

import com.undercut.game.nxt.DoActionOpcode
import com.undercut.script.event.Event

class ManualDoAction(val opcode: DoActionOpcode, val target: Any) : Event