package solutions._2023

import doNothing
import solutions._2023.Pulse.HIGH_PULSE
import solutions._2023.Pulse.LOW_PULSE

private const val BROADCASTER = "broadcaster"

enum class Pulse { LOW_PULSE, HIGH_PULSE }

data class PulseCounter(
    var lowPulses: Long = 0,
    var highPulses: Long = 0
) {
    fun product() = lowPulses * highPulses
}

sealed class Module {
    abstract val name: String
    abstract val nextModules: List<String>

    abstract fun send(
        allModules:
        Map<String, Module>,
        processingModules: MutableList<String>,
        pulseCounter: PulseCounter,
        printing: Boolean
    ): Pulse?
    abstract fun receive(from: String, pulse: Pulse)

    data class FlipFlop(
        override val name: String,
        override val nextModules: List<String>
    ) : Module() {
        private val pulsesReceived = mutableListOf<Pulse>()
        private var isOn = false

        override fun receive(from: String, pulse: Pulse) {
            pulsesReceived.add(pulse)
        }

        override fun send(
            allModules: Map<String, Module>,
            processingModules: MutableList<String>,
            pulseCounter: PulseCounter,
            printing: Boolean
        ): Pulse? {
            return if (pulsesReceived.isNotEmpty()) {
                val pulse = pulsesReceived.removeFirst()

                when {
                    pulse == LOW_PULSE && isOn -> {
                        isOn = false
                        nextModules.forEach {
                            allModules.getValue(it).receive(this.name, LOW_PULSE)
                            pulseCounter.lowPulses++
                            if (printing) println("$name -low-> $it")
                        }
                        processingModules.addAll(nextModules)
                        LOW_PULSE
                    }
                    pulse == LOW_PULSE -> {
                        isOn = true
                        nextModules.forEach {
                            allModules.getValue(it).receive(this.name, HIGH_PULSE)
                            pulseCounter.highPulses++
                            if (printing) println("$name -high-> $it")
                        }
                        processingModules.addAll(nextModules)
                        HIGH_PULSE
                    }
                    pulse == HIGH_PULSE -> {
                        doNothing
                        //if (printing) println("$name -> ENDED")
                        null
                    }
                    else -> null
                }
            } else {
                null
            }
        }

        override fun toString(): String = "%$name -> $nextModules"
    }

    data class Conjunction(
        override val name: String,
        override val nextModules: List<String>
    ) : Module() {
        private val pulsesReceived: MutableMap<String, Pulse> = mutableMapOf()
        fun senders() = pulsesReceived.keys

        override fun receive(from: String, pulse: Pulse) {
            pulsesReceived[from] = pulse
        }

        override fun send(
            allModules: Map<String, Module>,
            processingModules: MutableList<String>,
            pulseCounter: PulseCounter,
            printing: Boolean
        ): Pulse? {
            return if (pulsesReceived.isNotEmpty()) {
                processingModules.addAll(nextModules)
                if (pulsesReceived.values.all { it == HIGH_PULSE }) {
                    nextModules.forEach {
                        allModules.getValue(it).receive(this.name, LOW_PULSE)
                        pulseCounter.lowPulses++
                        if (printing) println("$name -low-> $it")
                    }
                    LOW_PULSE
                } else {
                    nextModules.forEach {
                        allModules.getValue(it).receive(this.name, HIGH_PULSE)
                        pulseCounter.highPulses++
                        if (printing) println("$name -high-> $it")
                    }
                    HIGH_PULSE
                }
            } else {
                null
            }
        }

        override fun toString(): String = "&$name -> $nextModules"
    }

    data class Broadcaster(
        override val nextModules: List<String>
    ) : Module() {
        private val pulsesReceived: MutableMap<String, Pulse> = mutableMapOf()
        override val name: String = BROADCASTER

        override fun receive(from: String, pulse: Pulse) {
            pulsesReceived[from] = pulse
        }

        override fun send(
            allModules: Map<String, Module>,
            processingModules: MutableList<String>,
            pulseCounter: PulseCounter,
            printing: Boolean
        ): Pulse? {
            return if (pulsesReceived.isNotEmpty()) {
                val pulseToSend = pulsesReceived.values.first()
                val isPulseHigh = pulseToSend == HIGH_PULSE
                nextModules.forEach {
                    allModules.getValue(it).receive(this.name, pulseToSend)
                    if (isPulseHigh) {
                        pulseCounter.highPulses++
                        if (printing) println("$name -high-> $it")
                    } else {
                        pulseCounter.lowPulses++
                        if (printing) println("$name -low-> $it")
                    }
                }
                processingModules.addAll(nextModules)
                pulsesReceived.clear()
                pulseToSend
            } else {
                null
            }
        }

        override fun toString(): String = "$name -> $nextModules"
    }

    data class Control(override val name: String) : Module() {
        override val nextModules: List<String> = emptyList()
        override fun receive(from: String, pulse: Pulse) { }
        override fun send(allModules: Map<String, Module>, processingModules: MutableList<String>, pulseCounter: PulseCounter, printing: Boolean) = null
        override fun toString(): String = name
    }
}

object ButtonModule {
    fun press(allModules: Map<String, Module>, processingModules: MutableList<String>, pulseCounter: PulseCounter, printing: Boolean) {
        allModules.getValue(BROADCASTER).receive("buttonModule", LOW_PULSE)
        processingModules.add(BROADCASTER)
        pulseCounter.lowPulses++
        if (printing) println("button -low-> $BROADCASTER")
    }
}

fun createModules(input: Sequence<String>): Map<String, Module> {
    val modules = input
        .toList()
        .map { raw ->
            val firstSplit = raw.split(" -> ")
            val name = firstSplit[0]
            val nextModules = firstSplit.getOrNull(1)?.split(", ") ?: emptyList()

            when {
                name == BROADCASTER -> Module.Broadcaster(nextModules)
                name.startsWith("%") -> Module.FlipFlop(name.drop(1), nextModules)
                name.startsWith("&") -> Module.Conjunction(name.drop(1), nextModules)
                else -> Module.Control(name)
            }
        }.associateBy { it.name }
    val dummyModules = mutableMapOf<String, Module>()

    modules.values.forEach { module ->
        module.nextModules.forEach { nextModuleName ->
            val nextModule = modules[nextModuleName]

            when (nextModule) {
                is Module.Conjunction -> nextModule.receive(module.name, LOW_PULSE)
                null -> dummyModules[nextModuleName] = Module.Control(nextModuleName)
                else -> doNothing
            }
        }
    }
    return modules + dummyModules
}

fun calculateNumberOfMultipliedPulses(input: Sequence<String>) {
    val allModules = createModules(input)
    allModules.forEach { println(it) }

    val processingModules = mutableListOf<String>()
    val pulseCounter = PulseCounter()

    repeat(1000) {
        println()
        ButtonModule.press(allModules, processingModules, pulseCounter, true)

        while (processingModules.isNotEmpty()) {
            val nextProcessingModules = mutableListOf<String>()

            for (moduleName in processingModules) {
                allModules.getValue(moduleName).send(allModules, nextProcessingModules, pulseCounter, true)
            }
            processingModules.clear()
            processingModules.addAll(nextProcessingModules)
        }
        println(pulseCounter)
    }

    println("The product of high and low pulses is ${pulseCounter.product()}")
}

fun pressButtonTillComplete(input: Sequence<String>) {
    val allModules = createModules(input)

    val processingModules = mutableListOf<String>()
    val pulseCounter = PulseCounter()
    val nameOfModuleSearched = "rx"

    val moduleToSearch = allModules.values.first { it.nextModules.contains(nameOfModuleSearched) } as Module.Conjunction
    val buttonPressesWhenModuleReceivesHighPulse = moduleToSearch.senders()
        .associateWith { LoopControl() }
        .toMutableMap()

    var buttonPresses = 0L

    while (buttonPressesWhenModuleReceivesHighPulse.values.any { !it.control() }) {
        buttonPresses++
        ButtonModule.press(allModules, processingModules, pulseCounter, false)

        while (processingModules.isNotEmpty()) {
            val nextProcessingModules = mutableListOf<String>()

            for (moduleName in processingModules) {
                val pulseSent = allModules.getValue(moduleName).send(allModules, nextProcessingModules, pulseCounter, false)

                if (pulseSent == HIGH_PULSE && buttonPressesWhenModuleReceivesHighPulse.keys.contains(moduleName)) {
                    buttonPressesWhenModuleReceivesHighPulse[moduleName]?.add(buttonPresses)
                }
            }
            processingModules.clear()
            processingModules.addAll(nextProcessingModules)
        }

        println("Button presses $buttonPresses")
        buttonPressesWhenModuleReceivesHighPulse.entries.forEach { if (it.value.control()) println(it) }
    }

    val buttonPressesToExit = buttonPressesWhenModuleReceivesHighPulse.values
        .map{ it.firstHit }
        .reduce { acc: Long, value: Long -> findLCM(acc, value)  }
    println("Min button presses to exit $buttonPressesToExit")
}

data class LoopControl(var firstHit: Long = 0L, var secondHit: Long = 0L) {
    fun add(hit: Long) {
        if (firstHit == 0L) {
            firstHit = hit
        } else if (firstHit > 0L && secondHit == 0L) {
            secondHit = hit
        }
    }

    fun control() = if (firstHit > 0 && secondHit > 0) {
        secondHit - firstHit == firstHit
    } else {
        false
    }
}