export interface PlayerModel {
    id: number,
    lobbyOwner: boolean,
    name: string,
    role: string,
    side: string,
    rdyState: boolean,
    passed: boolean,
}