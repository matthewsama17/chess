package model;

import chess.ChessGame;
import chess.ChessGame.TeamColor;

public record GameData(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game, TeamColor resigned) { }
