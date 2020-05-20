import webstomp, {Client, Message, SubscribeHeaders} from 'webstomp-client';

import {config} from '@/config'
import SockJS from "sockjs-client";

let client: Client;

// TODO: rewrite this using class syntax
export const connect = async () => {
    const socket = new SockJS(config.baseUrl);
    client = webstomp.over(socket);
    return new Promise((resolve, reject) => {
        //  kikapcsolja a loggolást
        //   client.debug = () => {
        //       null
        //   };
        client.connect({}, () => {
            resolve()
        });
    })
}

export const send = async (path, body): Promise<void> => {
    if (!client) await connect()
    return client.send(path, JSON.stringify(body))
}


export const subscribe = async (path: string, callback: (body?: object) => void, headers: SubscribeHeaders = {}) => {
    if (!client) await connect()
    const wrappedCallback = (message: Message) => callback(JSON.parse(message.body))
    return client.subscribe(path, wrappedCallback, headers)
}