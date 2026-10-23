export interface Bloc {
  idBloc: number;
  nomBloc: string;
  capaciteBloc: number;
}

export interface BlocPayload {
  nomBloc: string;
  capaciteBloc: number;
}

export interface BlocUpdatePayload extends BlocPayload {
  idBloc: number;
}
