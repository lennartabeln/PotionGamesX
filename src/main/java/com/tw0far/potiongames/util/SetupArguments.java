package com.tw0far.potiongames.util;

/**
 * Parses the optional lobby number of the setup commands.
 *
 * <p>Multi-lobby servers use the long form ({@code /pg addarena <lobby> <arena>}).
 * With {@code pg.singleLobby: true} the legacy short form without a lobby number
 * is accepted as well and resolves to lobby 1, so an upgraded single-lobby setup
 * keeps working with the old command shapes.</p>
 */
public final class SetupArguments {

    private SetupArguments() {
    }

    /** Lobby number plus the index of the first argument that is not the lobby number. */
    public record LobbyArguments(int lobbyId, int valueIndex) {
    }

    /**
     * Resolves the lobby argument of a command that takes {@code <lobby> <name>}.
     *
     * @param singleLobby whether {@code pg.singleLobby} is enabled
     * @param args        the raw command arguments ({@code args[0]} is the subcommand)
     * @return the parsed lobby and the index of the name argument, or {@code null} when the
     *         arguments do not fit the active mode
     */
    public static LobbyArguments parse(boolean singleLobby, String[] args) {
        if (args == null || args.length >= 3) {
            return args == null ? null : parseLongForm(args);
        }
        // Short form: "/pg addarena <arena>" - only valid in single-lobby mode.
        if (args.length == 2 && singleLobby) {
            return new LobbyArguments(1, 1);
        }
        return null;
    }

    private static LobbyArguments parseLongForm(String[] args) {
        try {
            return new LobbyArguments(Integer.parseInt(args[1]), 2);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    /**
     * Resolves the lobby argument of a command that only takes a lobby number.
     *
     * @param singleLobby whether {@code pg.singleLobby} is enabled
     * @param args        the raw command arguments
     * @return the lobby number, or {@code null} when the arguments do not fit the active mode
     */
    public static Integer parseLobbyOnly(boolean singleLobby, String[] args) {
        if (args == null) {
            return null;
        }
        if (args.length >= 2) {
            try {
                return Integer.parseInt(args[1]);
            } catch (NumberFormatException ex) {
                return null;
            }
        }
        // Short form: "/pg joinsign" - only valid in single-lobby mode.
        return singleLobby ? Integer.valueOf(1) : null;
    }
}
